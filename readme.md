# Miaou Is An Orchestral Unifier
## Procédure d'instalation : M.I.A.O.U
### Initialisation du raspberry py
1 - Se munir d'un raspberry.

2 - Le câbler comme suit : ![schema_du_projet.jpg](./Schéma_du_projet.jpg)

3 - Après avoir connecté le raspberry à internet, cloner ce repository dessus.

4 - Verifiez que sur le raspberry pi que python3 et java17 soient fonctionnels.

### Installation des dépendances
1 - Dépendances de python : 

Executer les commandes suivantes dans le dossier MIAOU/MIAOU_Bot:
- `python3 -m venv .`
- `bin/pip install gitpython`
- `bin/pip install RPI.GPIO`
- `bin/pip install yt-dlp`
2 - Dépendance de Java :
  
Télécharger et ajouter les fichiers suivant dans MIAOU/MIAOU_Bot/lib/ (Maven/Gradle c'est pour les faibles).
- `commons-collections4-4.4.jar`
- `jackson-core-2.13.5.jar`
- `JDA-6.1.2.jar`
- `kotlin-stdlib-1.8.0.jar`
- `okhttp-4.9.3.jar`
- `slf4j-simple-1.7.36.jar`
- `jackson-annotations-2.13.5.jar`
- `jackson-databind-2.13.5.jar`
- `jl1.0.1.jar`
- `env-websocket-client-2.14.jar`
- `okio-2.8.0.jar`
- `eslf4j-api-1.7.36.jar`
- `trove4j-3.0.3.jar`
- Une version de tous ces jar est disponible dans le MIAOU/MIAOU_Bot/lib/ de ce repo (ce n'est pas clone automatiquement)
### Lancement du Bot discord
1 - Ajouter un fichier token.token dans le dossier MIAOU_Bot contenant le token de connexion du bot discord.

2 - Utiliser le liens d'invitation fournis à la création du Bot discord pour inviter le bot sur votre serveur favoris.

3 - Lancer le bot grace à la commande `/bin/python /src/miaou.py`.

4 - Profitez. (Utiliser `!miaou help` dans un channel discord visible par le bot pour en savoir plus sur les fonctionalités de MIAOU).

