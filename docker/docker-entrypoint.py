#!python

import sys, os, subprocess

PRE = "NUTZBOOK_"
LINES = ""
ROOT = os.environ['CATALINA_HOME'] + "/webapps/ROOT/WEB-INF/classes/custom/"
print "ENV---------------------------"
for k,v in os.environ.items():
  if k.startswith(PRE) :
    if not v :
      continue
    K = k[len(PRE):]
    print K
    fname = ROOT + K[:K.index('.')] + ".properties"
    LINE = K + "=" + v
    print LINE, fname
    with open(fname, "a") as f :
        f.write("\n")
        f.write(LINE)

print "OAUTH---------------------------"
PRE = "OAUTH_"
fname = os.environ['CATALINA_HOME'] + "/webapps/ROOT/WEB-INF/classes/oauth_consumer.properties"
if os.environ.get(PRE + "api.github.com.consumer_key") :
  for k,v in os.environ.items():
    if k.startswith(PRE) :
      if not v :
        continue
      K = k[len(PRE):]
      LINE = K + "=" + v
      print LINE
      with open(fname, "a") as f :
        f.write("\n")
        f.write(LINE)

print "NGROK--------------------------"
PRE = "NGROK_"
fname = "/ngrok.yml"
if os.environ.get(PRE + "auth_token") :
  with open(fname, "w") as f:
    pass
  for k,v in os.environ.items():
    if k.startswith(PRE) :
      if not v :
        continue
      K = k[len(PRE):]
      LINE = K + " : " + v
      print LINE
      with open(fname, "a") as f :
        f.write("\n")
        f.write(LINE)
  subprocess.call("/ngrok -config /ngrok.yml -log none 8080 &", shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)

subprocess.call("catalina.sh run", shell=True)
