import RPi.GPIO as GPIO
import subprocess
import time

portled = 21
portcapteur = 18
GPIO.setmode(GPIO.BCM)
GPIO.setup(portled,GPIO.OUT,initial=GPIO.LOW)
GPIO.setup(portcapteur,GPIO.IN,pull_up_down = GPIO.PUD_UP)
print("super")
allume = False
GPIO.output(portled,GPIO.LOW)
print("debut compilation")
#proc = subprocess.Popen("make compile", shell=True)
#proc.wait()
print("fin compilation")
pid = 0;
while True:
    if (GPIO.input(portcapteur)==0 and allume):
        GPIO.output(portled,GPIO.LOW)
        print("detection fermeture")
        proc = subprocess.Popen("killall java", shell=True)
        allume = False
        
    elif (not allume and GPIO.input(portcapteur)==1):
        GPIO.output(portled,GPIO.HIGH)
        allume = True
        proc = subprocess.Popen("make run", shell=True)
        print("detection ouverture")
    time.sleep(1)
