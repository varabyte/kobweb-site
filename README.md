# Kobweb Site

This is a [Kobweb](https://github.com/varabyte/kobweb) project for generating the Kobweb site itself.

The goal of this site will be to highlight the features provided by the framework and the place to read documentation
about it.

## **Build Docker image and push to GCR**

- Make sure the Dockerfile is downloading the latest kobweb binary and do a export.
  For some reason export didn’t work inside docker container, so we do it here before building docker image<br />
  ```kobweb export```

- Build docker image<br />
  ```docker build -t kobweb-site .```

- Incase you first want to make sure the website runs locally<br />
  ```docker run -p 8080:8080 -it kobweb-site```

- Gcloud config to push the image to google cloud registry<br />
  ```gcloud auth login``` <br />
  ```gcloud auth configure-docker```

- Tag the image- kobweb-example-website-1 here is the GCP project id<br />
  ```docker tag kobweb-site gcr.io/kobweb-example-website-1/kobweb-website```

- Push the image<br />
  ```docker push gcr.io/kobweb-example-website-1/kobweb-website``` <br /><br />

# Deploy the docker container on Google Kubernetes Engine
## Deploy for the first time
- Go to the project kobweb-example-website and open cloud shell OR
  set up gcloud locally. If you’re setting up locally on your computer, make sure <br />
  ```gcloud auth list``` shows the correct active account

- Create GKE cluster in the specified zone and GCP project<br />
  ```gcloud container clusters create kobweb-site-cluster --zone=us-west1-a --project=kobweb-example-website-1```

- Fetch cluster endpoint and auth config<br />
  ```gcloud container clusters get-credentials kobweb-site-cluster --zone us-west1-a --project=kobweb-example-website-1```

- Create a new deployment<br />
  ```kubectl apply -f  gcp-deployment-config.yaml```

- Verify deployment created<br />
  ```kubectl get deployment```

- Verfiy pod created<br />
  ```kubectl get pods```

- create a static IP address named kobweb-site-ip<br />
    ```gcloud compute addresses create kobweb-site-ip --region us-west1 --project kobweb-example-website-1```

- To find the static IP address you created, run the following command, Copy the 'address' to use as load-balancer-ip in the next command:<br />
  ```gcloud compute addresses describe kobweb-site-ip --region us-west1 --project kobweb-example-website-1```

- Copy pod name from previous command and create/expose the service on port 80, this will generate an external IP where we can access the website<br />
  ```kubectl expose pod <POD_NAME> --port=80 --target-port 8080 --name=kobweb-site-service --type=LoadBalancer --load-balancer-ip=<STATIC_IP>```

- Verfiy service created and copy the external IP. It can take a few seconds for this IP to show up<br />
  ```kubectl get services```

- Set horizontal autoscaling on the deployment, set the maximum number of replicas to 10 and the minimum to 2, with a CPU utilization target of 50% utilization<br />
  ```kubectl autoscale deployment kobweb-site --max 10 --min 2 --cpu-percent 50```

- Go to the web browser and open the external IP, it should show the website. It can take a few seconds to show up though because it's finishing up running ```kobweb run``` internally at this point. You can go to the GCP console to see the deployment logs.<br />
  Kubernetes Engine->Workloads->kobweb-site->Logs

