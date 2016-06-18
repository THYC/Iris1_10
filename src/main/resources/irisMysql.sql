CREATE TABLE IF NOT EXISTS irisplayer
(
    playerUUID          CHAR(50)   	NOT NULL,
    playerName     	CHAR(50)   	NOT NULL,
    adresseIP           CHAR(50),
    playerLevel    	INT	    	NOT NULL,
    playerIP      	CHAR(30),
    money               DOUBLE DEFAULT 5,
    id_metier           INT DEFAULT 0   NOT NULL, 
    password            CHAR(50),
    blockBreak          INT DEFAULT 0   NOT NULL,
    blockBuild          INT DEFAULT 0   NOT NULL,
    woodBreak           INT DEFAULT 0   NOT NULL,
    woodPlant           INT DEFAULT 0   NOT NULL,
    CulturePlant        INT DEFAULT 0   NOT NULL,
    eleveur             INT DEFAULT 0   NOT NULL,
    PRIMARY KEY (playerUUID),
    UNIQUE KEY (playerUUID)
	);

CREATE TABLE IF NOT EXISTS irismouchard
(
    id_time             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    playerName     	CHAR(50)   	NOT NULL,
    adresseIP           CHAR(50),
    blockBreak          INT DEFAULT 0   NOT NULL,
    location            CHAR(255)
	);

CREATE TABLE IF NOT EXISTS irismessagename
(
    messagename         CHAR(50)        NOT NULL, 
    groupeperm      	CHAR(50) DEFAULT 'default',
    actif               INT DEFAULT 1   NOT NULL, 
    PRIMARY KEY (messagename),
    UNIQUE KEY (messagename)
	);

CREATE TABLE IF NOT EXISTS irislocation
(
    id_location		INT NOT NULL AUTO_INCREMENT,
    locationName	CHAR(30)    NOT NULL,
    playerUUID          CHAR(50) DEFAULT 'NC',
    world		CHAR(30)	NOT NULL,
    X			DOUBLE		NOT NULL,
    Y			DOUBLE		NOT NULL,
    Z			DOUBLE		NOT NULL,
    id_type		INT		NOT NULL,
    PRIMARY KEY (id_location)
    );

CREATE TABLE IF NOT EXISTS irisprotect
(
    pass        	CHAR(30)    NOT NULL,
    playerUUID          CHAR(50) DEFAULT 'NC',
    world		CHAR(30)	NOT NULL,
    X			DOUBLE		NOT NULL,
    Y			DOUBLE		NOT NULL,
    Z			DOUBLE		NOT NULL,
    id_type		INT		NOT NULL,
    PRIMARY KEY (X,Y,Z,world)
    );
	
CREATE TABLE IF NOT EXISTS irisplayerworld
(
	playerUUID		CHAR(50)                NOT NULL,
	world			CHAR(30)                NOT NULL,
        health                  DOUBLE                  ,
        hunger                  INT                     ,
        saturation              FLOAT                   ,
        experience              FLOAT                   ,
        totalexperience         INT                     ,
        plevel                  INT                     ,
        
	PRIMARY KEY (playerUUID, world)
	);

CREATE TABLE IF NOT EXISTS irisinventory
(
	id_itemsPlace		INT                     NOT NULL,
	playerUUID		CHAR(50)                NOT NULL,
        world			CHAR(30)                NOT NULL,
	items_type		INT                     ,
        items_durability        INT                     ,
	items_amount		INT                     ,
        items_enchantment       CHAR(255)               ,
	PRIMARY KEY (id_itemsPlace, playerUUID, world)
	);

CREATE TABLE IF NOT EXISTS irisworld
(
	world                   CHAR(30)                NOT NULL,
	groupWorld              INT			NOT NULL,
	PRIMARY KEY (world),
        UNIQUE KEY (world)
	);

CREATE TABLE IF NOT EXISTS irisportal
(
	portalName		CHAR(30)                NOT NULL,
	world			CHAR(30)                NOT NULL,
	X1			INT			NOT NULL,
	Y1			INT			NOT NULL,
	Z1			INT			NOT NULL,
	X2			INT			NOT NULL,
	Y2			INT			NOT NULL,
	Z2			INT			NOT NULL,
	toworld			CHAR(30),
	toX1			INT,
	toY1			INT,
	toZ1			INT,
	bookName		CHAR(30),
	message			CHAR(255),
        mode                    INT DEFAULT 0,
	PRIMARY KEY (portalName),
	UNIQUE KEY (portalName)
	);

CREATE TABLE IF NOT EXISTS irisparcelle
(
	parcelleName		CHAR(30)                NOT NULL,
        parent                  CHAR(30),
	world			CHAR(30)                NOT NULL,
	X1			INT			NOT NULL,
	Y1			INT			NOT NULL,
	Z1			INT			NOT NULL,
	X2			INT			NOT NULL,
	Y2			INT			NOT NULL,
	Z2			INT			NOT NULL,
	jail                    INT DEFAULT 0,
        noEnter                 INT DEFAULT 0,
        noFly                   INT DEFAULT 0,
        noBuild                 INT DEFAULT 1,
        noBreak                 INT DEFAULT 1,
        noTeleport              INT DEFAULT 0,
        noInteract              INT DEFAULT 0,
        noFire                  INT DEFAULT 1,
	message			CHAR(255),
        mode                    INT DEFAULT 0,
        mobspawn                INT DEFAULT 1,
        mobkill                 INT DEFAULT 0,
	PRIMARY KEY (parcelleName),
	UNIQUE KEY (parcelleName)
	);

CREATE TABLE IF NOT EXISTS irisparcellemember
(
        playerUUID          CHAR(50)                    NOT NULL,
        playerName          CHAR(50)                    NOT NULL,
        parcelleName        CHAR(30)                    NOT NULL,
        typeMember          INT DEFAULT 0               NOT NULL,
        PRIMARY KEY (playerUUID, parcelleName)
	);

CREATE TABLE IF NOT EXISTS irishorde
(
        hordeName           CHAR(30)                    NOT NULL,
        world               CHAR(50)                    NOT NULL,
        X1                  INT                         NOT NULL,
	Y1                  INT                         NOT NULL,
	Z1                  INT                         NOT NULL,
        kills               INT DEFAULT 0               NOT NULL,
        dead                INT DEFAULT 0               NOT NULL,
        PRIMARY KEY (hordeName)
	);

CREATE TABLE IF NOT EXISTS irishordemember
(
        playerUUID          CHAR(50)                    NOT NULL,
        playerName          CHAR(50)                    NOT NULL,
        hordeName           CHAR(30)                    NOT NULL,
        typeMember          INT DEFAULT 0               NOT NULL,
        power               INT(10) DEFAULT 10          NOT NULL,
        PRIMARY KEY (playerUUID)
	);

CREATE TABLE IF NOT EXISTS irischunk
(
        playerUUID              CHAR(50) DEFAULT 'NC'   NOT NULL,
        hordename               CHAR(50) DEFAULT 'NC'   NOT NULL,
        world			CHAR(30)                NOT NULL,
	X1			INT                     NOT NULL,
	Z1			INT                     NOT NULL,
	typeMember              INT DEFAULT 0,
        jail                    INT DEFAULT 0,
        noBuild                 INT DEFAULT 1,
        noBreak                 INT DEFAULT 1,
        noInteract              INT DEFAULT 0,
        noFire                  INT DEFAULT 1,
        noPVP                   INT DEFAULT 0,
        noKillAnimal            INT DEFAULT 1,
	message			CHAR(255),
        
        PRIMARY KEY (hordename, world, X1, Z1)
	);