# Building a CRUD Application with Angular, Spring Boot, and Kubernetes (Dockerized)

This README provides a step-by-step guide to deploying the backend (Spring Boot application) and MySQL database on a local Kubernetes cluster using Minikube. The guide includes console outputs for verification and troubleshooting.

## Required Tools

- **Minikube**: For running a local Kubernetes cluster.
- **kubectl**: Kubernetes command-line tool for interacting with the cluster.
- **Docker**: For building and managing container images (DockerHub is used as the registry for pulling base images).

Ensure these tools are installed and configured on your system before proceeding.

## Prerequisites

- Windows 11 (or compatible OS).
- Docker Desktop installed and running.
- Minikube installed (version v1.37.0 or later).
- kubectl installed and configured.
- The backend project (Spring Boot) with a `Dockerfile` and built JAR in `target/`.
- A `db-deployment.yaml` file for MySQL deployment (refer to the provided content).

## Step-by-Step Backend Deployment Guide

### Step 1: Check Minikube Version
Verify that Minikube is installed correctly.

```
D:\angular-springboot-crud-example-main\backend>minikube version
minikube version: v1.37.0
commit: 65318f4cfff9c12cc87ec9eb8f4cdd57b25047f3
```

### Step 2: Start Minikube Cluster
Start the Minikube cluster using the Docker driver.

```
D:\angular-springboot-crud-example-main\backend>minikube start --driver=docker

* minikube v1.37.0 on Microsoft Windows 11 Home Single Language 10.0.26200.7171 Build 26200.7171
* Using the docker driver based on existing profile
* Starting "minikube" primary control-plane node in "minikube" cluster
* Pulling base image v0.0.48 ...
* Restarting existing docker container for "minikube" ...
! Failing to connect to https://registry.k8s.io/ from inside the minikube container
* To pull new external images, you may need to configure a proxy: https://minikube.sigs.k8s.io/docs/reference/networking/proxy/
* Preparing Kubernetes v1.34.0 on Docker 28.4.0 ...
* Verifying Kubernetes components...
  - Using image gcr.io/k8s-minikube/storage-provisioner:v5
* Enabled addons: storage-provisioner, default-storageclass
* Done! kubectl is now configured to use "minikube" cluster and "default" namespace by default
```

### Step 3: Verify Minikube Status
Check the status of the Minikube cluster.

```
D:\angular-springboot-crud-example-main\backend>minikube status
minikube
type: Control Plane
host: Running
kubelet: Running
apiserver: Running
kubeconfig: Configured
```

### Step 4: Check Cluster Information
Verify the Kubernetes control plane and services.

```
D:\angular-springboot-crud-example-main\backend>kubectl cluster-info
Kubernetes control plane is running at https://127.0.0.1:53404
CoreDNS is running at https://127.0.0.1:53404/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy

To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.
```

### Step 5: List Nodes
Ensure the node is ready.

```
D:\angular-springboot-crud-example-main\backend>kubectl get node
NAME       STATUS   ROLES           AGE   VERSION
minikube   Ready    control-plane   62m   v1.34.0
```

### Step 6: Configure Docker Environment for Minikube
Set up Docker to use Minikube's daemon.

```
D:\angular-springboot-crud-example-main\backend>minikube docker-env
SET DOCKER_TLS_VERIFY=1
SET DOCKER_HOST=tcp://127.0.0.1:53405
SET DOCKER_CERT_PATH=C:\Users\mrsac\.minikube\certs
SET MINIKUBE_ACTIVE_DOCKERD=minikube
REM To point your shell to minikube's docker-daemon, run:
REM @FOR /f "tokens=*" %i IN ('minikube -p minikube docker-env --shell cmd') DO @%i
```

Apply the environment variables (run the REM command if needed).

### Step 7: List Docker Images
Check existing images in Minikube's Docker daemon.

```
D:\angular-springboot-crud-example-main\backend>docker images
                                                                                                                                                      i Info →   U  In Use
IMAGE                                 ID             DISK USAGE   CONTENT SIZE   EXTRA
gcr.io/k8s-minikube/kicbase:v0.0.48   c6b5532e987b       1.31GB             0B    U
```

### Step 8: Build the Backend Docker Image
Build the Docker image for the Spring Boot backend.

```
D:\angular-springboot-crud-example-main\backend>docker build -t backend:01 .
[+] Building 10.5s (8/8) FINISHED                                                                                                                    docker:desktop-linux
 => [internal] load build definition from Dockerfile                                                                                                                 0.4s
 => => transferring dockerfile: 181B                                                                                                                                 0.3s
 => [internal] load metadata for docker.io/library/eclipse-temurin:17-jdk                                                                                            5.4s
 => [auth] library/eclipse-temurin:pull token for registry-1.docker.io                                                                                               0.0s
 => [internal] load .dockerignore                                                                                                                                    0.0s
 => => transferring context: 2B                                                                                                                                      0.0s
 => [internal] load build context                                                                                                                                    4.0s
 => => transferring context: 46.31MB                                                                                                                                 3.9s
 => CACHED [1/2] FROM docker.io/library/eclipse-temurin:17-jdk@sha256:7995efb7f9276fc16433aa8e2856a06082cd09f7f6603579db2534937ccc6778                               0.0s
 => => resolve docker.io/library/eclipse-temurin:17-jdk@sha256:7995efb7f9276fc16433aa8e2856a06082cd09f7f6603579db2534937ccc6778                                      0.0s
 => [2/2] COPY target/*.jar app.jar                                                                                                                                  0.2s
 => exporting to image                                                                                                                                               0.3s
 => => exporting layers                                                                                                                                              0.3s
 => => writing image sha256:602fc835a888ab8a8db01873475045ec5d7551f479a90de097406a8c2b0d568e                                                                         0.0s
 => => naming to docker.io/library/backend:01                                                                                                                        0.0s

View build details: docker-desktop://dashboard/build/desktop-linux/desktop-linux/h460fl571gc8b6x0srkl4mzu6

What's next:
    View a summary of image vulnerabilities and recommendations → docker scout quickview
```

### Step 9: Verify Built Image
List images to confirm the backend image is built.

```
D:\angular-springboot-crud-example-main\backend>docker images
                                                                                                                                                      i Info →   U  In Use
IMAGE                                 ID             DISK USAGE   CONTENT SIZE   EXTRA
backend:01                            602fc835a888        466MB             0B
gcr.io/k8s-minikube/kicbase:v0.0.48   c6b5532e987b       1.31GB             0B    U
```

### Step 10: Load Image into Minikube
Load the built image into Minikube's cache.

```
D:\angular-springboot-crud-example-main\backend>minikube image load backend:01
```

### Step 11: Create Backend Deployment
Deploy the backend application.

```
D:\angular-springboot-crud-example-main\backend>kubectl create deployment backend-deployment --image=backend:01 --port=8080
deployment.apps/backend-deployment created
```
 ### NOTE : While the deployment object can be created using command-line commands, a dedicated app-deployment.yml file is required to configure and enable proper communication between the Spring Boot application and MySQL.

### Step 12: Check Deployment Status
Verify the deployment.

```
D:\angular-springboot-crud-example-main\backend>kubectl get deployment
NAME                 READY   UP-TO-DATE   AVAILABLE   AGE
backend-deployment   1/1     1            1           9s
```

### Step 13: Check Pods
Ensure the pod is running.

```
D:\angular-springboot-crud-example-main\backend>kubectl get pod
NAME                                 READY   STATUS             RESTARTS   AGE
backend-deployment-5cff586dd-4bt46   1/1     Running            0          19s
```

### Step 14: Expose the Deployment
Expose the backend service as a NodePort.

```
D:\angular-springboot-crud-example-main\backend>kubectl expose deployment backend-deployment --type=NodePort
service/backend-deployment exposed
```

### Step 15: Check Services
Verify the service is created.

```
D:\angular-springboot-crud-example-main\backend>kubectl get service
NAME                 TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
backend-deployment   NodePort    10.109.157.147   <none>        8080:31516/TCP   25s
kubernetes           ClusterIP   10.96.0.1        <none>        443/TCP          77m
```

### Step 16: Get Service URL
Obtain the URL to access the backend service.

```
D:\angular-springboot-crud-example-main\backend>minikube service backend-deployment --url
http://127.0.0.1:50764
! Because you are using a Docker driver on windows, the terminal needs to be open to run it.
```

### Step 17: Access Minikube Dashboard (Optional)
Open the Kubernetes dashboard for monitoring.

```
D:\angular-springboot-crud-example-main\backend>minikube dashboard
* Enabling dashboard ...
  - Using image docker.io/kubernetesui/dashboard:v2.7.0
  - Using image docker.io/kubernetesui/metrics-scraper:v1.0.8
* Some dashboard features require the metrics-server addon. To enable all features please run:

        minikube addons enable metrics-server

* Verifying dashboard health ...
* Launching proxy ...
* Verifying proxy health ...
* Opening http://127.0.0.1:61017/api/v1/namespaces/kubernetes-dashboard/services/http:kubernetes-dashboard:/proxy/ in your default browser...
^C
```

## MySQL Database Deployment

### Step 18: Deploy MySQL
Apply the MySQL deployment YAML file.

```
D:\angular-springboot-crud-example-main\backend>kubectl apply -f .\db-deployment.yaml
persistentvolumeclaim/mysql-pv-claim unchanged
deployment.apps/mysql unchanged
service/mysql unchanged
```

### Step 19: Check Pods (Including MySQL)
Verify both backend and MySQL pods are running.

```
PS D:\angular-springboot-crud-example-main\backend> kubectl get pod
NAME                                    READY   STATUS    RESTARTS      AGE
backend-deployment-5cff586dd-66ckj      1/1     Running   1 (12d ago)   14d
mysql-5bdfd74f8d-nnd7p                  1/1     Running   0             6m52s
```

### Step 20: Access MySQL via kubectl exec
Connect to the MySQL pod and interact with the database.

```
PS D:\angular-springboot-crud-example-main\backend> kubectl exec -it mysql-5bdfd74f8d-nnd7p -- mysql -u root -p
Enter password:
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 2
Server version: 5.7.44 MySQL Community Server (GPL)

Copyright (c) 2000, 2023, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> SHOW DATABASES;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| empdb              |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
5 rows in set (0.03 sec)

mysql> exit
```

### Accessing MySQL via Dashboard
1. Open the dashboard: `minikube dashboard`
2. Navigate to **Workloads → Pods**.
3. Click on the MySQL pod.
4. Click **Exec**.
5. Select:
   - Container: mysql
   - Shell: /bin/sh
6. Run: `mysql -u root -p` inside the terminal.

## Step-by-Step Angular Deployment Guide
🚧 In Progress 🚧
The Angular frontend deployment guide is currently under development. Stay tuned for updates!

## Notes
- The backend service is accessible via the URL provided by `minikube service backend-deployment --url`.
- Ensure the terminal remains open when using the Docker driver on Windows.
- If you encounter network issues, configure a proxy as suggested in the Minikube output.
- For production, consider using persistent volumes and secrets for sensitive data like database passwords. 

---
