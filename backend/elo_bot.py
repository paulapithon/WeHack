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


import os
import sys
import json
import requests

try:
    import apiai
except ImportError:
    sys.path.append(
        os.path.join(os.path.dirname(os.path.realpath(__file__)), os.pardir)
    )
    import apiai

from flask import Flask, request

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
                    
                    #print (msg.encode('utf-8'))
                    #size = range(len(response_obj["entry"][0]["messaging"])/2)
                    #if size == []:
                    #  size = [0]
                      
                    send_message(sender_id,msg)  
                    # print (size)
                    # for i in size:
                    #   msg = response_obj["entry"]["messaging"][i]["message"]["text"].encode('utf-8')
                    #   print msg
                    #   #print (range(len(response_obj["result"]["fulfillment"]["messages"])))
                             
                    #   send_message(sender_id, msg)
                    #   #verificar se usuário confirmou 
                    #   if "voce agora sera notificado" in msg:
                    #     adicionar_notificaveis(sender_id)
                    #   #ou cancelou
                    #   elif "você não receberá mais notificações" in msg:
                    #     remover_notificaveis(sender_id)
                    
                    # print ("ACABOU")
                    

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
    print(data["message"]["text"])
    msg_text = data["message"]["text"];
    msg_id = data["message"]["chat"]["id"]
    #send_message_telegram(msg_text, str(msg_id));
    
    #dialogflow
    req = ai.text_request()
    req.session_id = msg_id
    req.query = msg_text
    response = req.getresponse()
    responsestr = response.read().decode('utf-8')
    response_obj = json.loads(responsestr)
    
    print(response_obj)
    
    msg = response_obj["result"]["fulfillment"]["messages"][0]["speech"].encode('utf-8')
      
    send_message_telegram(msg, str(msg_id))  
    
    return "ok", 200
    
@app.route('/telegram', methods=['GET'])
def telegram_get():
    return "Works", 200

def get_url(url):
    response = requests.get(url)
    content = response.content.decode("utf8")
    return content

def send_message_telegram(text, chat_id):
    url = URL + "/sendMessage?chat_id=" + chat_id + "&text=" + text
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


def log(message):  # simple wrapper for logging to stdout on heroku
    print str(message)
    sys.stdout.flush()

def check():
    data_de_hoje = time.strftime("%d/%m/%Y") #tem que atualizar algumas vezes durante o dia
    ano = time.strftime("%Y")
    print (data_de_hoje)

    url_str = "http://www.camara.leg.br/SitCamaraWS/Proposicoes.asmx/ListarProposicoesVotadasEmPlenario?ano=" + ano + "&tipo="
    xml_str = urllib2.urlopen(url_str).read()
    xmldoc = minidom.parseString(xml_str)
    proposicoes= xmldoc.getElementsByTagName('proposicao')

    for proposicao in proposicoes:
        data =  proposicao.getElementsByTagName('dataVotacao').item(0).firstChild.nodeValue
        if data == data_de_hoje:
          #ok, isso foi votado hoje
          print data

          #agora receber mais informações
          #vamos precisar do tipo, do número e do ano
          #tudo isso esta na informação "nomeProposicao" : <nomeProposicao>PL 4302/1998</nomeProposicao>
          #vamos dividir por espaço e depois dividir por /
          nomeProposicao =  proposicao.getElementsByTagName("nomeProposicao").item(0).firstChild.nodeValue
          
          
          #agora verificar se essa proposicao não já foi notificada
          file  = open('ditos.txt', 'r') 
          if nomeProposicao in file.read():
            file.close()
            #ja foi dito
          else:
            file.close()
            file  = open('ditos.txt', 'a') 
            file.write(nomeProposicao + "\n")
            file.close()
          
            tipo = nomeProposicao.split(' ')[0]
            numero = nomeProposicao.split(' ')[1].split('/')[0]
            ano =  nomeProposicao.split(' ')[1].split('/')[1]

            url_str = "http://www.camara.leg.br/SitCamaraWS/Proposicoes.asmx/ObterProposicao?tipo=" + tipo + "&numero=" + numero + "&ano=" + ano
            xml_str = urllib2.urlopen(url_str).read()
            xmldoc = minidom.parseString(xml_str)

            tema = xmldoc.getElementsByTagName('tema').item(0).firstChild.nodeValue.encode('utf-8')
            ementa = xmldoc.getElementsByTagName('Ementa').item(0).firstChild.nodeValue.encode('utf-8')
            link = xmldoc.getElementsByTagName('LinkInteiroTeor').item(0).firstChild.nodeValue.encode('utf-8')

            notificar(nomeProposicao, tema, ementa, link)

            print nomeProposicao
            print tema
            print ementa
            print link + "\n\n"
          
    threading.Timer(3600, check).start() #3600 = 1h
    

if __name__ == '__main__':
    port = int(os.getenv('PORT', 3000))
    print("Starting app on port %d" % port)
    check()
    app.run(debug=False, port=port, host='0.0.0.0')