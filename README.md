# ⛷️ SkiStation – Microservices Configuration & Docker Deployment   

## 📌 Introduction  
This repository contains the **configuration and deployment setup** for the SkiStation project  
The main focus is on **Docker-based containerization** and orchestration of the microservices that make up the ski station management platform 
The microservices themselves are developed in separate repositories, while this repo provides the **infrastructure layer** for running them together  

---

## 🏗️ System Architecture  
<img width="4000" height="2250" alt="SKI-Management-MicroService-Architecture-1" src="https://github.com/user-attachments/assets/91f589ea-a4cd-47cb-b4c7-2eac1ce1d826" />


The SkiStation platform is composed of the following microservices:  

- **Course Management Service** – Handles ski courses creation, updates, and assignments
- **Instructor Management Service** – Manages ski instructors and their associations with courses  
- **Piste Management Service** – Maintains piste (ski slope) information and conditions  
- **Registration Management Service** – Handles skier registrations for courses and events  
- **Skier Management Service** – Manages skier profiles and related information  
- **Subscription Management Service** – Handles skier subscriptions and memberships

Each service runs as an independent **Docker container**, allowing modularity, scalability, and ease of deployment

---

## 🔎 Service Discovery & API Gateway  

- **Eureka Discovery Server**  
  - Central registry where all microservices register themselves  
  - Enables dynamic service lookup instead of hardcoding service URLs  
  - Provides resilience and load balancing across services  

- **Spring Cloud API Gateway**  
  - Acts as the single entry point for all client requests  
  - Routes requests to the corresponding microservice via Eureka  
  - Supports features like authentication, logging, and request filtering  

**Architecture Flow:**  
1. Each microservice registers with **Eureka Discovery Server**  
2. Clients send requests through the **API Gateway**  
3. The Gateway routes requests dynamically to the appropriate service  

---

## 🛠️ Technologies & Tools  

- **Microservices Architecture** – Modular, independent services  
- **Maven** – Dependency management & build tool (used in services)  
- **Mockito** – Unit testing framework (used in services)  
- **Docker** – Containerization of services  
- **Docker Compose** – Orchestration of multiple services in a single environment  
- **Docker Hub** – Storage and distribution of container images  

---

## ▶️ Usage  

### Clone the Repository  
```bash
git clone [https://github.com/Youssefesprit/Cloud-Native-SkiStation-Platform.git](https://github.com/Youssefesprit/Cloud-Native-SkiStation-Platform)
cd Cloud-Native-SkiStation-Platform
```

## 👨‍💻 Team Members  

This project was developed by 6 students as part of the **DevOps course**:  

- [Chaima Eljed](https://github.com/chaimjed) 
- [Chaima Saadallah](https://github.com/saadallahchaima)
- [Jouhayna Cheikh](https://github.com/Jouhayna-Cheikh) 
- [Malek Labidi](https://github.com/maleklabidi)
- [Maher Karoui](https://github.com/MaherKaroui)
- [Youssef Farhat](https://github.com/Youssefesprit)

---

## 📜 License  
This project is developed in an **academic context** and is intended for **educational purposes only**  

---
