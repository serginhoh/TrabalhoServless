## AWS Serverless Cloud Native Java RESTful API

Trabalho final da disciplina Serverless Architecture.
 
## Pré-requisitos

* AWS CLI instalado e configurado e no minímo com PowerUser permission
* [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Docker](https://www.docker.com/community-edition)
* [Maven](https://maven.apache.org/install.html)
* [SAM CLI](https://github.com/awslabs/aws-sam-cli)
* [Python 3](https://docs.python.org/3/)

## Configuração

### Dependências

`Maven` para atualizar as dependências e gerar o pacote JAR

```bash
mvn install
```

### Deploy local

**Utilização local do API Gateway / DynamoDB**
1. Utilizaremos uma instância local do DynamoDB em um container Docker: 

	`docker run -d -p 8000:8000 -v $(pwd)/local/dynamodb:/data/ amazon/dynamodb-local -jar DynamoDBLocal.jar -sharedDb -dbPath /data` 

    - no Windows PowerShell:

	`docker run -d -p 8000:8000 -v ${pwd}/local/dynmodb:/data/ amazon/dynamodb-local -jar DynamoDBLocal.jar -sharedDb -dbPath /data` 

2. Criação da tabela no DynamoDB:

    `aws dynamodb create-table --table-name trip --attribute-definitions AttributeName=country,AttributeType=S AttributeName=date,AttributeType=S AttributeName=city,AttributeType=S --key-schema AttributeName=country,KeyType=HASH AttributeName=date,KeyType=RANGE --local-secondary-indexes 'IndexName=cityIndex,KeySchema=[{AttributeName=country,KeyType=HASH},{AttributeName=city,KeyType=RANGE}],Projection={ProjectionType=ALL}' --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000`

    - Caso necessário apagar a tabela existente:
    
        `aws dynamodb delete-table --table-name trip --endpoint-url http://localhost:8000`

3. Start da SAM local API.
* Mac: `sam local start-api --env-vars src/test/resources/test_environment_mac.json`

* Windows: `sam local start-api --env-vars src/test/resources/test_environment_windows.json`<br>
    - alterar 'docker.for.windows.localhost' pelo IP atual  (obtido pelo comando ipconfig).		
	
* Linux: `sam local start-api --env-vars src/test/resources/test_environment_linux.json`
 
Se os comandos acima foram executados com sucesso, agora será possível acessar o seguinte endpoint :
 
`http://localhost:3000/trips/findbyperiod?starts=2020-01-01&ends=2020-01-31`

O retorno HTTP deve ser `200` com um objeto Trip vazio. `[{"country":null,"date":null,"city":null,"reason":null}]`

Foi disponibilizado o arquivo src/test/resources/TrabalhoServless.postman_collection.json do Postman contendo todas as chamadas as APIs disponíveis.

## Deploy AWS

Precisaremos de um bucket S3 para fazer o upload do pacote da nossa aplicação.

```bash
export BUCKET_NAME=Nome_do_Bucket
    * windows PowerShell $BUCKET_NAME="Nome_do_Bucket"

aws s3 mb s3://$BUCKET_NAME
```
O próximo passo é criar o package no S3

```bash
sam package --template-file template.yaml --output-template-file packaged.yaml --s3-bucket $BUCKET_NAME
```

Em seguida utilizaremos o Cloudformation para efetuar o Deploy

```bash
sam deploy --template-file packaged.yaml --stack-name trabalhoserverless --capabilities CAPABILITY_IAM
```

Após o deploy executar o seguinte comando para obter o Endpoint do API Gateway.
 
O endereço está disponível em  `"OutputKey": "ProdDataEndpoint"`: 
```bash
aws cloudformation describe-stacks --stack-name trabalhoserverless --query 'Stacks[].Outputs'
```
