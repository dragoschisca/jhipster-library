# Library App

## 1) Configurare

### Cerințe
- Java 21
- Maven
- Node.js + npm
- Docker 
- PostgreSQL 

### Instalare dependințe frontend
```bash
npm install
```

---

## 2) Compilare (build)

### Build frontend
```bash
npm run webapp:build
```

### Build backend (prod)
```bash
./mvnw -Pprod -DskipTests
```

---

## 3) Lansare aplicație (dev)

### Backend + frontend 
```bash
./mvnw -Pdev -DskipTests
```

Aplicația va fi disponibilă la:
```
http://localhost:8080
```

### Live reload (frontend separat)
```bash
npm start
```

Frontend:
```
http://localhost:9000
```

---

## 5) Rulare container local (după deploy)

```bash
docker run -p 8080:8080 dragos31/library:latest
```

---

## 6) Useri disponibili
- **admin**  
  username: `admin`  
  password: `admin`

- **user**  
  username: `user`  
  password: `user`

- **librarian**  
  username: `testlibrarian`  
  password: `test`

- **librarian**  
  username: `testlibrarian2`  
  password: `test`

---

## 7) Observații importante
- Dacă modifici UI și nu vezi schimbări:
  ```bash
  npm run webapp:build
  ```
  apoi hard refresh (Ctrl+Shift+R).
