package com.MiniProject.backend;

import com.MiniProject.backend.model.Product;
import com.MiniProject.backend.model.User;
import com.MiniProject.backend.repository.ProductRepository;
import com.MiniProject.backend.repository.UserRepository;
import com.MiniProject.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Test
	@DisplayName("Context loads and verifies MySQL database connection")
	void contextLoadsAndVerifiesMySQLConnection() throws SQLException {
		assertNotNull(dataSource, "DataSource should not be null");
		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();
			String productName = metaData.getDatabaseProductName();
			System.out.println("Connected Database: " + productName + " " + metaData.getDatabaseProductVersion());
			System.out.println("JDBC Driver: " + metaData.getDriverName() + " " + metaData.getDriverVersion());
			assertTrue(productName.toLowerCase().contains("mysql"), "Must connect to MySQL database");
		}
	}

	@Test
	@DisplayName("Verify products exist in MySQL and CRUD works")
	void testProductRepository() {
		List<Product> products = productRepository.findAll();
		assertNotNull(products);
		assertTrue(products.size() >= 32, "Initial seed products should be at least 32");

		// Test saving a temporary product
		Product temp = new Product("TestProduct_" + UUID.randomUUID().toString().substring(0, 8), 10);
		Product saved = productRepository.save(temp);
		assertNotNull(saved.getId());

		// Test finding product
		Product found = productRepository.findById(saved.getId()).orElse(null);
		assertNotNull(found);
		assertEquals(temp.getName(), found.getName());

		// Test deleting product
		productRepository.deleteById(saved.getId());
		assertFalse(productRepository.findById(saved.getId()).isPresent());
	}

	@Test
	@DisplayName("Verify User signup, login, and retrieval via MySQL")
	void testUserSignupAndLogin() {
		String testUser = "user_" + UUID.randomUUID().toString().substring(0, 8);
		String testPass = "pass123";

		// Signup
		String signupResult = userService.signup(new User(testUser, testPass));
		assertEquals("User registered successfully!", signupResult);

		// Duplicate signup
		String dupResult = userService.signup(new User(testUser, "otherPass"));
		assertEquals("Username already exists!", dupResult);

		// Valid login
		String validLoginResult = userService.login(new User(testUser, testPass));
		assertEquals("Login successful!", validLoginResult);

		// Invalid login
		String invalidLoginResult = userService.login(new User(testUser, "wrongPassword"));
		assertEquals("Invalid username or password!", invalidLoginResult);

		// Get all usernames includes testUser
		List<String> usernames = userService.getAllUsernames();
		assertTrue(usernames.contains(testUser));

		// Clean up test user
		User persistedUser = userRepository.findByUsername(testUser);
		if (persistedUser != null) {
			userRepository.deleteById(persistedUser.getId());
		}
	}

}
