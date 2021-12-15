# Kobweb Site

This is a [Kobweb](https://github.com/varabyte/kobweb) project for generating the Kobweb site itself.

The goal of this site will be to highlight the features provided by the framework and the place to read documentation
about it.

## **Build Docker image and push to GCR**

- Build docker image<br />
  ```docker build -t kobweb-site .```

- Incase you first want to make sure the website runs locally<br />
  ```docker run -p 8080:8080 kobweb-site```

- Gcloud config to push the image to google cloud registry<br />
  ```gcloud auth login``` <br />
  ```gcloud auth configure-docker```

- Tag the image- kobweb-example-website-1 here is the GCP project id. Increment the tag number when you push an update<br />
  ```docker tag kobweb-site gcr.io/kobweb-example-website-1/kobweb-website:1```

- Push the image to the GCP Container Registry<br />
  ```docker push gcr.io/kobweb-example-website-1/kobweb-website:1``` <br /><br />

# Deploy the docker container using Cloud Run
- Run the following command to deploy your app:<br />
  ```gcloud run deploy kobweb-site-service --image=gcr.io/kobweb-example-website-1/kobweb-website:1 --platform managed --region us-central1 --memory 1024Mi --allow-unauthenticated --project kobweb-example-website-1``` <br /><br />
  **kobweb-example-website-1** is the GCP project name and <br /> **gcr.io/kobweb-example-website-1/kobweb-website:1** is the GCR image you want to deploy