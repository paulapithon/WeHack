#!/usr/bin/env python
# -*- coding: utf-8 -*-




#API.AI
CLIENT_ACCESS_TOKEN = "6e81055fab12458fb03cee39bbec72bd"


#FACEBOOK
VERIFY_TOKEN = "ghtgfg38#"
PAGE_ACCESS_TOKEN = "EAADjbVfm4fEBAObWLdDI6H90WNFVTjSSrMEe02Pcbq66HWfWH9ZBZA0FYGvvc9PWtTGwF6Ouat6V1Bx2bJtsFFYVtnfau3fyzTkQjZCHKrweO05gptoX4xt8RSBVeA7ON3knMxIlAU9jzv24Ju8cpW3QexDtRnqNmlZB9iszUgZDZD"

#TELEGRAM
TELEGRAM_TOKEN = "620374745:AAF-pRZCG-P3r9IGdLZsImT5JRx6W_cNLhI"
URL = "https://api.telegram.org/bot" + TELEGRAM_TOKEN
import telebot
tb = telebot.TeleBot(TELEGRAM_TOKEN)


import os
import sys
import json
import requests

#QRCode
import qrcode
from flask import Flask, request

try:
    import apiai
except ImportError:
    sys.path.append(
        os.path.join(os.path.dirname(os.path.realpath(__file__)), os.pardir)
    )
    import apiai


app = Flask(__name__)

ai = apiai.ApiAI(CLIENT_ACCESS_TOKEN)


#lista de quem está sendo notificado
galera  = []

#lista das mensagens ja notificadas
ditos   = []


#verificar mensagens a cada 1 hora
from xml.dom import minidom
import urllib2
import time
import threading



def notificar(nomeProposicao, tema, ementa, link):
    with open('galera.txt', 'r') as f:
      for id in f:
        print id
  #fazer hoje só com um array, amanha coloco um banco de dados


@app.route('/', methods=['GET'])
def verify():
    # when the endpoint is registered as a webhook, it must echo back
    # the 'hub.challenge' value it receives in the query arguments
    if request.args.get("hub.mode") == "subscribe" and request.args.get("hub.challenge"):
        if not request.args.get("hub.verify_token") == VERIFY_TOKEN:
            return "Verification token mismatch", 403
        return request.args["hub.challenge"], 200

    return "Hello world", 200


@app.route('/', methods=['POST'])
def webhook():

    # endpoint for processing incoming messaging events

    data = request.get_json()
    
    log(data)  # you may not want to log every incoming message in production, but it's good for testing

    if data["object"] == "page":

        for entry in data["entry"]:
            for messaging_event in entry["messaging"]:

                if messaging_event.get("message"):  # someone sent us a message

                    sender_id = messaging_event["sender"]["id"]        # the facebook ID of the person sending you the message
                    recipient_id = messaging_event["recipient"]["id"]  # the recipient's ID, which should be your page's facebook ID
                    message_text = messaging_event["message"]["text"]  # the message's text
                    print messaging_event
                    print message_text.encode('utf-8')
                    #o que o API.AI diz para a gente dizer?
                    req = ai.text_request()
                    req.session_id = sender_id
                    req.query = message_text#"oi" #trocar isso por message_text
                    response = req.getresponse()
                    responsestr = response.read().decode('utf-8')
                    response_obj = json.loads(responsestr)
                    print (responsestr)
                    msg = response_obj["result"]["fulfillment"]["messages"][0]["speech"].encode('utf-8')
                      
                    send_message(sender_id,msg)  

                if messaging_event.get("delivery"):  # delivery confirmation
                    pass

                if messaging_event.get("optin"):  # optin confirmation
                    pass

                if messaging_event.get("postback"):  # user clicked/tapped "postback" button in earlier message
                    pass

    return "ok", 200


@app.route('/telegram', methods=['POST'])
def telegram():
    data = request.get_json()
    print(data);
    if "text" not in data["message"]:
        return "not text", 200
    print(data["message"]["text"])
    msg_text = data["message"]["text"];
    msg_id = data["message"]["chat"]["id"]

    #dialogflow
    req = ai.text_request()
    req.session_id = msg_id
    req.query = msg_text
    response = req.getresponse()
    responsestr = response.read().decode('utf-8')
    response_obj = json.loads(responsestr)
    
    print(response_obj)
    
    #itirate all messages
    for m in response_obj["result"]["fulfillment"]["messages"]:
        if "platform" in  json.dumps(m):
            print(json.dumps(response_obj["result"]))

            ##Add Quick Replies
            if "replies" in json.dumps(m):
               quickReplies(msg_id, m["title"].replace("$user_name",data["message"]["from"]["first_name"]), m["replies"])
            ##Send Message
            else:         
                send_message_telegram(m["speech"].replace("$user_name",data["message"]["from"]["first_name"]).encode('utf-8'), str(msg_id))  
       
    ##Execute Actions
    if "action" in json.dumps(response_obj["result"]):
        action = response_obj["result"]["action"]
        print("action = " + action);
        if action == "gerar_qr_code":
            if "last_name" in json.dumps(data["message"]["from"]):
                img = qrcode.make('{"id":'+str(msg_id)+',"nome":"'+data["message"]["from"]["first_name"].encode('utf-8') + " " + data["message"]["from"]["last_name"].encode('utf-8') +'", "cpf":"110.558.284-20","email":""}').save("imgs/" + str(msg_id) +  str(data["message"]["date"]) + '.png')
            else:
                img = qrcode.make('{"id":'+str(msg_id)+',"nome":"'+data["message"]["from"]["first_name"].encode('utf-8')+'", "cpf":"110.558.284-20","email":""}').save("imgs/" + str(msg_id) +  str(data["message"]["date"]) + '.png')
           
            url = URL + "/sendPhoto?chat_id=" + str(msg_id) + "&photo=https://elo-michaelbarney.c9users.io/imgs/" + str(msg_id) +  str(data["message"]["date"]) + ".png"
            print(url)
            get_url(url)
    
    return "ok", 200
    
@app.route('/telegram', methods=['GET'])
def telegram_get():
    return "works", 200
    
def get_url(url):
    response = requests.get(url)
    content = response.content.decode("utf8")
    return content

def send_message_telegram(text, chat_id):
    url = URL + "/sendMessage?chat_id=" + chat_id + "&text=" + text
    print(url);
    get_url(url)

def adicionar_notificaveis(sender_id):
    #verificar se já está na galera
    file  = open('galera.txt', 'r') 
    if sender_id in file.read():
      send_message(sender_id, "Ops, parece que você já está inscrito nas notificações!")
      file.close()
    #adicinar na galera
    else:
      file.close()
      file  = open('galera.txt', 'a') 
      file.write(sender_id + "\n")
      file.close()
    
def remover_notificaveis(sender_id):
    #verificar se já está na galera
    f  = open('galera.txt', 'r') 
    lines = f.readlines()
    f.close()
    f = open("galera.txt","w")
    for line in lines:
      if line!=sender_id+"\n":
        f.write(line)
    f.close()
  

def send_message(recipient_id, message_text):

    log("sending message to {recipient}: {text}".format(recipient=recipient_id, text=message_text))

    params = {
        "access_token": PAGE_ACCESS_TOKEN
    }
    headers = {
        "Content-Type": "application/json"
    }
    data = json.dumps({
        "recipient": {
            "id": recipient_id
        },
        "message": {
            "text": message_text
        }
    })
    r = requests.post("https://graph.facebook.com/v2.6/me/messages", params=params, headers=headers, data=data)
    if r.status_code != 200:
        log(r.status_code)
        log(r.text)

def quickReplies(chat_id, title, replies):
    types = telebot.types #
    markup = types.ReplyKeyboardMarkup(one_time_keyboard=True)
    for reply in replies:
        markup.add(types.KeyboardButton(reply))
    tb.send_message(chat_id, title, reply_markup=markup)

@app.route("/imgs/<path:path>")
def images(path):
    fullpath = "./imgs/" + path
    resp = app.make_response(open(fullpath).read())
    resp.content_type = "image/jpeg"
    return resp

def log(message):  # simple wrapper for logging to stdout on heroku
    print str(message)
    sys.stdout.flush()
    

##PAYMENT ROUTES
@app.route('/payment', methods=['POST'])
def payment():
    data = json.loads(request.get_json())
    #data = request.get_json()
    print(data)
    uid = data["id"]
    name = data["nome"]
    valor = data["wallet"]
    
    Id = request.headers.get('MerchantId')
    Key = request.headers.get('MerchantKey')
    print("ID: " + str(Id) + " KEY " + str(Key))
    ##do the payment
    params = {
    }
    headers = {
        "MerchantId": Id,
        "MerchantKey": Key,
        "Content-Type": "application/json"
    }
    data = json.dumps({
           "MerchantOrderId":"2014111703",
           "Customer":{
              "Name": name
           },
           "Payment":{
             "Type":"CreditCard",
             "Amount":valor,
             "Installments":1,
             "SoftDescriptor":"123456789ABCD",
             "CreditCard":{
                 "CardNumber":"0000000000000001",
                 "Holder":name,
                 "ExpirationDate":"12/2030",
                 "SecurityCode":"123",
                 "Brand":"Visa"
             }
           }
    })
    r = requests.post("https://apisandbox.cieloecommerce.cielo.com.br/1/sales", params=params, headers=headers, data=data)
    print(r.status_code)
    if r.status_code != 201:
        log(r.status_code)
        log(r.text)
        print("deu ruim")
        return "deu ruim", 300
    
    print("deu bem, enviando")
   #send_message_telegram("Compra finalizada na Accenture Happy Hour. Custo final: " + str(valor), uid)
    url = URL + "/sendMessage?chat_id=" + uid + "&text=" + "Compra finalizada na Accenture Happy Hour. Custo final: " + str(valor)
    print(url);
    get_url(url)
    print("enviado");
    return '{"ok": "ok"}', 200

@app.route('/update', methods=['POST'])
def update():
    data = json.loads(request.get_json())
    #data = request.get_json()
    print(data)
    uid = data["id"]
    name = data["nome"]
    valor = data["wallet"]
    
    Id = request.headers.get('MerchantId')
    Key = request.headers.get('MerchantKey')
    print("ID: " + str(Id) + " KEY " + str(Key))
    
    
    ##update the JSON
    with open(str(uid) + '.json', 'w') as outfile:  
        json.dump(data, outfile)
    return '{"ok": "ok"}', 200

##chatbot commands
@tb.message_handler(commands=['dividir'])
def handle_dividir(message):
    send_message_telegram("Conta dividida iniciada! Digite \participar para entrar.")

if __name__ == '__main__':
    port = int(os.getenv('PORT', 3000))
    print("Starting app on port %d" % port)
    app.run(debug=False, port=port, host='0.0.0.0')
    