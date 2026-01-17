import RPi.GPIO as GPIO
import subprocess
import time
import sys, os
import git


path = os.path.dirname(sys.argv[0])
os.chdir(path+"/../")
g = git.cmd.Git("./../")
g.pull()

portcapteur = 18
GPIO.setmode(GPIO.BCM)
GPIO.setup(portcapteur,GPIO.IN,pull_up_down = GPIO.PUD_UP)
allume = False
proc = subprocess.Popen("make compile", shell=True)
proc.wait()
print("fin compilation")
proc = subprocess.Popen("bash ./src/miaoudeur.sh -i", shell="True")
proc.wait()
pid = 0;
while True:
    if (GPIO.input(portcapteur)==0 and allume):
        print("detection fermeture")
        proc = subprocess.Popen("killall java", shell=True)
        proc = subprocess.Popen("bash ./src/miaoudeur.sh -c", shell="True")
        allume = False

    elif (not allume and GPIO.input(portcapteur)==1):
        allume = True
        proc = subprocess.Popen("make run", shell=True)
        print("detection ouverture")
    time.sleep(1)
