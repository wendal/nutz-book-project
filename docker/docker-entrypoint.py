#!python

import sys, os, subprocess

PRE = "NUTZBOOK_"
LINES = ""
ROOT = os.environ['CATALINA_HOME'] + "/webapps/ROOT/WEB-INF/classes/custom/"
print "ENV---------------------------"
for k,v in os.environ.items():
  if k.startswith(PRE) :
    K = k[len(PRE):]
    fname = ROOT + K[:K.index('.')] + ".properties"
    LINE = K + "=" + v
    print LINE, fname
    with open(fname, "a") as f :
        f.write("\n")
        f.write(LINE)
print "ENV---------------------------"

os.system("catalina.sh run")