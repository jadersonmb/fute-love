# Webhook WhatsApp + Express + Ngrok
Este projeto cria um **servidor Node.js com Express** para receber mensagens de um webhook, validar o token de verificação e encaminhar os dados recebidos para um backend.  
O **Ngrok** é utilizado para expor o servidor local para a internet.

---

## 🚀 Pré-requisitos

Antes de começar, você precisa ter instalado na sua máquina:

- [Node.js](https://nodejs.org/) (versão 18 ou superior recomendada)
- [npm](https://www.npmjs.com/) (vem junto com o Node.js)
- [Ngrok](https://ngrok.com/download) (para expor a porta local para a internet)
- Backend rodando em `http://localhost:8080/public/api/v1/whatsapp/webhook`

---
```
## 📦 Instalação do Projeto

### 1. Clone este repositório

git clone https://github.com/seu-usuario/seu-projeto.git


2. Instale as dependências
   npm install express axios

Copiar código : 

npm install express axios

3. Crie o arquivo index.js com o seguinte conteúdo
javascript
Copiar código

const express = require('express');
const axios = require('axios');

const app = express();

app.use(express.json());

const port = process.env.PORT || 3000;
const verifyToken = 'token';

const BACKEND_URL = 'http://localhost:8080/public/api/v1/whatsapp/webhook';

app.get('/', (req, res) => {
  const { 'hub.mode': mode, 'hub.challenge': challenge, 'hub.verify_token': token } = req.query;

  if (mode === 'subscribe' && token === verifyToken) {
    console.log('WEBHOOK VERIFIED');
    res.status(200).send(challenge);
  } else {
    res.status(403).end();
  }
});

app.post('/', async (req, res) => {
  const timestamp = new Date().toISOString().replace('T', ' ').slice(0, 19);
  console.log(`\n\nWebhook received ${timestamp}\n`);
  console.log(JSON.stringify(req.body, null, 2));

  try {
    const response = await axios.post(BACKEND_URL, req.body);
    console.log('➡️ Mensagem encaminhada ao backend:', response.status);
  } catch (err) {
    console.error('❌ Erro ao encaminhar para backend:', err.message);
  }

  res.status(200).end();
});

app.listen(port, () => {
  console.log(`\nListening on port ${port}\n`);
});
```
```
▶️ Como rodar o projeto
1. Suba o servidor Node usando o comando:
    node index.js
Listening on port 3000

2. Exponha a porta com Ngrok
Em outro terminal, execute:
    ngrok http 3000
Você verá algo como:
nginx
Forwarding                    https://random-id.ngrok.io -> http://localhost:3000
Copie a URL https://random-id.ngrok.io e configure como Webhook URL no painel do WhatsApp.
```
```
🖥️ Subindo o Backend
Certifique-se de que o backend esteja rodando em:

http://localhost:8080/api/v1/whatsapp/webhook
Se estiver usando Spring Boot, rode com:

./mvnw spring-boot:run
ou
mvn spring-boot:run
```

✅ Fluxo de funcionamento

O WhatsApp envia uma requisição para o endpoint público (Ngrok).  
O servidor Express recebe a requisição e faz o log.  
O corpo da requisição é encaminhado para o backend local.  
O backend processa os dados conforme sua regra de negócio.

📌 Exemplos de logs
Quando o webhook é verificado:

    WEBHOOK VERIFIED

Quando uma mensagem é recebida:

    Webhook received 2025-09-30 19:00:00
```
{
  "object": "whatsapp_business_account",
  "entry": [...]
}
```

➡️ Mensagem encaminhada ao backend: 200
Quando ocorre erro ao encaminhar:

❌ Erro ao encaminhar para backend: connect ECONNREFUSED 127.0.0.1:8080