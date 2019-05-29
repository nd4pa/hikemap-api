HikeMap Web Application
===================
> Ce code est soumis à une licence [GPLv3](LICENSE.md), This code is licensed under [GPLv3](LICENSE.md).

Ce dépot contient les source de L'API REST [Hikemap API](https://hikemap-api.blondeau.me), dont le d'effectuer les opérations de routage pour l'application web [Hikemap](https://hikemap.blondeau.me).

Dépendences
--------------------
### Librairies :
- [Graphhopper](https://github.com/graphhopper/graphhopper) version : 0.11.0
- [Google GSON](https://github.com/google/gson) version : 2.8.5
- [Java JSON](https://www.tutorialspoint.com/json/json_java_example.htm) version : 20180813
- [Slf4j](https://www.slf4j.org/) version: 1.7.21
- [SparkJava](http://sparkjava.com/) version: 2.7.2
- [Junit](https://junit.org/junit5/) version: 4.12

### APIs REST :
- [Overpass](https://wiki.openstreetmap.org/wiki/Overpass_API)

Pré-requis
-------------------
- Un serveur web capable de servir en https.
- Un sous-domaine (par exemple ```hikemap-api.mondomaine.net```)
- OpenJDK (>= 1.8), ```openjdk-8-jdk``` sous Debian/Ubuntu


Installation
------------------------
L'installation est relativement simple, elle peut être réaliser à l'aide de n'importe quelle serveur web capable de fournir du https.
> Le HTTPS est requis car il transporte des données de positionnement.

par volonté de simplification, ce tutoriel sera réaliser à l'aide de [Caddy Server](https://caddyserver.com/).

> Ce tutoriel à été réalisé et testé sous Ubuntu et Debian uniquement et est prévu uniquement pour des installations sous Linux.

### Installation de sources
Déplacez-vous dans le dossier de vôtre choix et clonez le dépot.
```bash
cd /chemin/vers/mon/dossier
git clone https://github.com/nd4pa/hikemap-api.git
```
> L'installation de caddy à été détaillé dans le dépôt de l'application web Hikemap : https://github.com/nd4pa/hikemap-front

### Création du fichier de configuration
```bash
vim /etc/caddy/Caddyfile
```
Ajouter la configuration à la fin du fichier en remplaçant ```@@NOM_DE_DOMAINE@@``` par le nom de domaine que vous avez choisi.

```
@@NOM_DE_DOMAINE@@ {
	proxy / 127.0.0.1:4567
}
```
### Compilation du projet
L'étape suivante va compiler les sources du projet, télécharger l'archive Openstreetmap pour le projet et mettre en place le build
> Remplacez ```@@CHEMIN_VERS_LE_DEPOT_PARENT@@``` par l'endroit ou vous désirez que le projet soit placé (par exemple : ```/srv/http```).
```bash
cd @@CHEMIN_VERS_LE_DEPOT_PARENT@@
git clone https://github.com/nd4pa/hikemap-api.git
```
>Notez ```@@CHEMIN_VERS_LE_DEPOT_PARENT@@/hikemap-api``` pour remplacer plus tard ```@@CHEMIN_VERS_LE_DEPOT@@``` par cette valeur (par exemple: ```/srv/http/hikemap-api```)

Placez vous dans le dépot cloné et compilez lancez la compilation, ce qui va effectuer les actions suivantes :
- Compilation du projet
- Téléchargement de l'archive openstreetmap (~= 300 Mo)
- Extraction du build

```bash
cd hikemap-api
./gradlew build
```

### Configuration du l'API comme démon
Afin d'éxecuter l'API comme démon, il faut créer un service systemd en remplaçant ```@@CHEMIN_VERS_LE_DEPOT@@``` par le chemin vers le dépôt (par exemple: ```/srv/http/hikemap-api```) , recharger systemd et activer le démarrage du serveur au boot, puis redémarrez caddy :

```
vim /etc/systemd/system/hikemap-api.service
```
Ajouter :
```
[Unit]
Description=Hikemap API
After=network-online.target
Wants=network-online.target systemd-networkd-wait-online.service

[Service]
Restart=on-abnormal

; User and group the process will run as.
User=root

; Always set "-root" to something safe in case it gets forgotten in the Caddyfile.
ExecStart=@@CHEMIN_VERS_LE_DEPOT@@/build/distributions/hikemap/bin/hikemap @@CHEMIN_VERS_LE_DEPOT@@/osm/data.osm.pbf @@CHEMIN_VERS_LE_DEPOT@@/graph

[Install]
WantedBy=multi-user.target
```

Exécutez :
```bash
systemctl daemon-reload
systemctl start hikemap-api.service
systemctl enable hikemap-api.service
systemctl restart caddy
```
Configuration de développement
----------------------------------------
Pour lancer l'API en configuration de développement, il suffit d'exécuter :
```bash
./gradlew run --args="osm/data.osm.pbf graph"
```
Si vous voulez nettoyer le build et supprimer le graph générer par graphhopper exécutez :
```bash
./gradlew clean
```

Contribution
--------------------
Tout fork, contribution ou issue est le/la bienvenue.
