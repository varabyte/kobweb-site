# Kobweb Site

This is a [Kobweb](https://github.com/varabyte/kobweb) project for generating the Kobweb site itself.

The goal of this site will be to highlight the features provided by the framework and the place to read documentation
about it.

# Deploy Site

### Google cloud shell
Make sure to include the app.json at the root of your repository

[![Run on Google Cloud](https://deploy.cloud.run/button.svg)](https://deploy.cloud.run)

### or run the following commands locally<br />

#### **Build Docker image and push to GCR**

- Build docker image<br />
  ```docker build -t kobweb-site .```

- Incase you first want to make sure the website runs locally<br />
  ```docker run -p 8080:8080 kobweb-site```

- Gcloud config to push the image to google cloud registry<br />
  ```gcloud auth login``` <br />
  ```gcloud auth configure-docker```

- Tag the image<br />
  ```docker tag kobweb-site gcr.io/kobweb-example-website-1/kobweb-site-service:1```

- Push the image to the GCP Container Registry<br />
  ```docker push gcr.io/kobweb-example-website-1/kobweb-site-service:1``` <br /><br />

#### Deploy the docker container using Cloud Run
Run the following command to deploy your app:
```
gcloud run deploy kobweb-site-service\
  --project=kobweb-example-website-1\
  --platform=managed\
  --region=us-central1\
  --image=gcr.io/kobweb-example-website-1/kobweb-site-service:1\
  --port=8080\
  --allow-unauthenticated\
  --memory=1024Mi\
  --cpu=1\
  --no-use-http2
  ```