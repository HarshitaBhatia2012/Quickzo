pipeline {
    agent any

    environment {
        COMPOSE_FILE = 'compose.yaml'
        REPO_URL     = 'https://github.com/HarshitaBhatia2012/Quickzo.git'
        BRANCH_NAME  = 'master'
        // Database connection settings for tests and container communication
        DB_HOST      = 'quickzo-mysql'
        DB_PORT      = '3306'
        DB_NAME      = 'quickzo'
        DB_USERNAME  = 'quickzo_user'
        DB_PASSWORD  = 'quickzo_password'
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out ${BRANCH_NAME} branch from ${REPO_URL}..."
                git branch: "${BRANCH_NAME}", url: "${REPO_URL}"
            }
        }

        stage('Validate Environment & Prerequisites') {
            steps {
                script {
                    echo 'Validating workspace structure, build files, and Docker engine...'

                    // 1. Verify required project files exist at expected locations
                    def requiredFiles = [
                        'compose.yaml',
                        'frontend/Dockerfile',
                        'frontend/nginx.conf',
                        'backend/Dockerfile',
                        'backend/backend/build.gradle',
                        'backend/backend/gradlew'
                    ]

                    for (file in requiredFiles) {
                        if (!fileExists(file)) {
                            error("Required project file missing: ${file}")
                        }
                    }
                    echo "All core project configuration files verified."

                    // 2. Validate Docker and Docker Compose availability
                    if (isUnix()) {
                        sh '''
                            echo "=== Docker CLI & Daemon Diagnostics ==="
                            which docker || { echo "ERROR: docker CLI not found in PATH"; exit 1; }
                            docker --version
                            docker compose version || { echo "ERROR: docker compose not available"; exit 1; }
                        '''
                    } else {
                        bat '''
                            @echo off
                            echo === Docker CLI & Daemon Diagnostics ===
                            where docker || (echo ERROR: docker CLI not found in PATH & exit /b 1)
                            docker --version
                            docker compose version || (echo ERROR: docker compose not available & exit /b 1)
                        '''
                    }
                }
            }
        }

        stage('Backend Build & Test') {
            steps {
                script {
                    echo 'Building Spring Boot application and running tests using backend/backend/gradlew...'
                    dir('backend/backend') {
                        if (isUnix()) {
                            sh '''
                                chmod +x ./gradlew

                                # Pre-check Gradle wrapper to prevent java.net.ConnectException redirect failure
                                WRAPPER_DIST_DIR="$HOME/.gradle/wrapper/dists/gradle-8.14.3-bin"
                                if [ ! -d "$WRAPPER_DIST_DIR"/*/*/bin ]; then
                                    echo "Pre-fetching Gradle distribution to prevent Java HttpURLConnection redirect timeout..."
                                    mkdir -p "$WRAPPER_DIST_DIR"
                                    ./gradlew --version >/dev/null 2>&1 || true
                                    TARGET_HASH=$(find "$WRAPPER_DIST_DIR" -maxdepth 1 -mindepth 1 -type d 2>/dev/null | head -n 1)
                                    if [ -n "$TARGET_HASH" ] && [ ! -d "$TARGET_HASH"/*/bin ]; then
                                        rm -f "$TARGET_HASH"/*.part "$TARGET_HASH"/*.lck
                                        if [ -f /tmp/gradle-8.14.3-bin.zip ]; then
                                            cp /tmp/gradle-8.14.3-bin.zip "$TARGET_HASH/gradle-8.14.3-bin.zip"
                                        else
                                            curl -s -L -f --retry 3 -o "$TARGET_HASH/gradle-8.14.3-bin.zip" https://services.gradle.org/distributions/gradle-8.14.3-bin.zip || true
                                        fi
                                    fi
                                fi

                                # Compile, run test suite, and generate executable Spring Boot JAR
                                ./gradlew clean test bootJar --no-daemon
                            '''
                        } else {
                            bat '''
                                gradlew.bat clean test bootJar --no-daemon
                            '''
                        }
                    }
                }
            }
            post {
                always {
                    // Archive JUnit test reports
                    junit testResults: 'backend/backend/build/test-results/test/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Docker Image Build') {
            steps {
                script {
                    echo "Building Docker container images from project root using ${COMPOSE_FILE}..."
                    if (isUnix()) {
                        sh "docker compose -f ${COMPOSE_FILE} build"
                    } else {
                        bat "docker compose -f ${COMPOSE_FILE} build"
                    }
                }
            }
        }

        stage('Deploy Services') {
            steps {
                script {
                    echo "Deploying application stack using ${COMPOSE_FILE}..."
                    echo "Preserving existing MySQL persistent volume (quickzo_mysql_data)..."

                    // Separate credentials resolution from deployment execution
                    def useJenkinsCredentials = false
                    try {
                        withCredentials([
                            usernamePassword(
                                credentialsId: 'quickzo-db-credentials',
                                usernameVariable: 'TEST_USER',
                                passwordVariable: 'TEST_PASS'
                            ),
                            string(
                                credentialsId: 'quickzo-mysql-root-password',
                                variable: 'TEST_ROOT_PASS'
                            )
                        ]) {
                            useJenkinsCredentials = true
                        }
                    } catch (Exception credErr) {
                        echo "Notice: Jenkins Credentials ('quickzo-db-credentials' / 'quickzo-mysql-root-password') not defined in Jenkins Store."
                        echo "Diagnostic details: ${credErr.message}"
                        useJenkinsCredentials = false
                    }

                    // Prepare runtime environment in a fresh workspace if .env does not exist
                    if (!fileExists('.env')) {
                        if (fileExists('.env.example')) {
                            echo "Notice: Fresh workspace detected without .env. Preparing runtime .env from .env.example..."
                            if (isUnix()) {
                                sh 'cp .env.example .env'
                            } else {
                                bat 'copy .env.example .env'
                            }
                        } else {
                            echo "Notice: Neither .env nor .env.example found; relying on compose.yaml environment defaults."
                        }
                    }

                    // Execute deployment without catching/suppressing deployment failures
                    if (useJenkinsCredentials) {
                        echo "Deploying with credentials injected from Jenkins Credentials Store..."
                        withCredentials([
                            usernamePassword(
                                credentialsId: 'quickzo-db-credentials',
                                usernameVariable: 'DB_USERNAME',
                                passwordVariable: 'DB_PASSWORD'
                            ),
                            string(
                                credentialsId: 'quickzo-mysql-root-password',
                                variable: 'MYSQL_ROOT_PASSWORD'
                            )
                        ]) {
                            if (isUnix()) {
                                sh "docker compose -f ${COMPOSE_FILE} up -d --remove-orphans"
                            } else {
                                bat "docker compose -f ${COMPOSE_FILE} up -d --remove-orphans"
                            }
                        }
                    } else {
                        echo "Deploying using workspace environment configuration..."
                        if (isUnix()) {
                            sh "docker compose -f ${COMPOSE_FILE} up -d --remove-orphans"
                        } else {
                            bat "docker compose -f ${COMPOSE_FILE} up -d --remove-orphans"
                        }
                    }
                }
            }
        }

        stage('Smoke Test & Health Verification') {
            steps {
                script {
                    echo 'Verifying service health and HTTP responses...'
                    if (isUnix()) {
                        sh '''
                            set -e
                            echo "=== Waiting for MySQL and Backend services to be healthy ==="
                            
                            # 1. Output container status for diagnostics
                            docker compose -f compose.yaml ps

                            # 2. Verify Frontend HTTP 200 response (fail if unreachable)
                            echo "Checking Frontend HTTP response (port 80)..."
                            FRONTEND_READY=0
                            for i in $(seq 1 15); do
                                HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://quickzo-frontend:80/ 2>/dev/null || \
                                            curl -s -o /dev/null -w "%{http_code}" http://localhost:80/ 2>/dev/null || \
                                            curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:80/ 2>/dev/null || \
                                            curl -s -o /dev/null -w "%{http_code}" http://host.docker.internal:80/ 2>/dev/null || \
                                            docker compose -f compose.yaml exec -T frontend wget -q -O /dev/null -S http://127.0.0.1/ 2>&1 | grep "HTTP/" | awk '{print $2}' || true)
                                if [ "$HTTP_CODE" = "200" ]; then
                                    echo "Frontend check PASSED: Received HTTP 200 OK"
                                    FRONTEND_READY=1
                                    break
                                fi
                                echo "Frontend not ready yet (HTTP ${HTTP_CODE:-none}, attempt ${i}/15). Retrying in 2s..."
                                sleep 2
                            done

                            if [ "$FRONTEND_READY" -ne 1 ]; then
                                echo "FATAL: Frontend health check FAILED! Service did not return HTTP 200."
                                exit 1
                            fi

                            # 3. Verify Backend API /products endpoint HTTP 200 response (fail if unreachable)
                            echo "Checking Backend API response on /products (port 8081 and reverse proxy port 80)..."
                            BACKEND_READY=0
                            for i in $(seq 1 20); do
                                API_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://quickzo-backend:8081/products 2>/dev/null || \
                                           curl -s -o /dev/null -w "%{http_code}" http://quickzo-frontend:80/products 2>/dev/null || \
                                           curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/products 2>/dev/null || \
                                           curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8081/products 2>/dev/null || \
                                           curl -s -o /dev/null -w "%{http_code}" http://localhost:80/products 2>/dev/null || \
                                           curl -s -o /dev/null -w "%{http_code}" http://host.docker.internal:8081/products 2>/dev/null || \
                                           docker compose -f compose.yaml exec -T backend wget -q -O /dev/null -S http://127.0.0.1:8081/products 2>&1 | grep "HTTP/" | awk '{print $2}' || true)
                                if [ "$API_CODE" = "200" ]; then
                                    echo "Backend API check PASSED: Received HTTP 200 OK on /products"
                                    BACKEND_READY=1
                                    break
                                fi
                                echo "Backend API not ready yet (HTTP ${API_CODE:-none}, attempt ${i}/20). Retrying in 2s..."
                                sleep 2
                            done

                            if [ "$BACKEND_READY" -ne 1 ]; then
                                echo "FATAL: Backend API health check FAILED! /products did not return HTTP 200."
                                exit 1
                            fi

                            echo "All smoke tests PASSED successfully."
                        '''
                    } else {
                        bat '''
                            @echo off
                            echo Waiting for services to stabilize...
                            timeout /t 5 /nobreak >nul
                            docker compose -f compose.yaml ps

                            echo Testing Frontend endpoint (port 80)...
                            curl -s -f -o nul http://localhost:80/
                            if %ERRORLEVEL% NEQ 0 (
                                echo FATAL: Frontend health check FAILED! Did not receive HTTP 200.
                                exit /b 1
                            )
                            echo Frontend check PASSED: Received HTTP 200.

                            echo Testing Backend API endpoint (/products)...
                            curl -s -f -o nul http://localhost:8081/products
                            if %ERRORLEVEL% NEQ 0 (
                                echo FATAL: Backend API health check FAILED on /products!
                                exit /b 1
                            )
                            echo Backend API check PASSED: Received HTTP 200 on /products.
                        '''
                    }
                }
            }
        }
    }

    post {
        success {
            echo '====================================================='
            echo ' Quickzo CI/CD Pipeline executed successfully!       '
            echo ' Frontend, Backend, and Database are up and running. '
            echo '====================================================='
        }
        failure {
            echo '====================================================='
            echo ' Pipeline FAILED! Gathering container logs...        '
            echo '====================================================='
            script {
                if (isUnix()) {
                    sh "docker compose -f ${COMPOSE_FILE} logs --tail=100 || true"
                } else {
                    bat "docker compose -f ${COMPOSE_FILE} logs --tail=100"
                }
            }
        }
    }
}
