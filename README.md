# Kobweb Site

This is a [Kobweb](https://github.com/varabyte/kobweb) project for generating the Kobweb site itself.

The goal of this site will be to highlight the features provided by the framework and the place to read documentation
about it.

# Docker set up
Following commands will set up all the necessary dependencies needed for this website project and run the website

`docker build --no-cache --progress=plain -t kobweb-site:v1 .
`
`docker run -p 8080:8080 -it kobweb-site:v1
`
