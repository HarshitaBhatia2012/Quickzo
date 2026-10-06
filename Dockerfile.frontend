FROM nginx:alpine

# Remove default nginx static page
RUN rm -rf /usr/share/nginx/html/*

# Copy custom reverse-proxy and static configuration
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf

# Copy static website files into Nginx document root
COPY Quickzo.html /usr/share/nginx/html/index.html
COPY *.html /usr/share/nginx/html/
COPY *.css /usr/share/nginx/html/
COPY *.js /usr/share/nginx/html/
COPY rider.jpeg /usr/share/nginx/html/
COPY Video.mp4 /usr/share/nginx/html/
COPY images/ /usr/share/nginx/html/images/

# Ensure both Style.css and style.css exist for case-sensitive Linux filesystems
RUN cp /usr/share/nginx/html/Style.css /usr/share/nginx/html/style.css 2>/dev/null || true

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
