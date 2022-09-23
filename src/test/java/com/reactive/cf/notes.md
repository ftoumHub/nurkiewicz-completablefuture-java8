Cette présentation à pour but de présenter non pas un langage ou une librairie mais bien 
une classe unique provenant de la version 8 de Java. Il s'agit de la classe CompletableFuture qui est une implémentation
de l'interface Future qui existe en Java depuis plusieurs versions.
C'est une classe qui permet d'écrire des programmes d'une façon extremement différentes de ce qu'on peut être amené 
à rencontrer, il devient donc essentiel de comprendre les fonctionnalités qu'elle propose.

Il n'y aura pas de slide pendant cette présentation!


01 - Intro

Appel d'un traitement potentiellement bloquant dans le thread principal = mauvaise idée!!

Dans un monde réactif, on a pas le droit de bloquer le thread principal!!

On décharge les traitements potentiellement bloquants dans un thread background (en utilisant un executor service)
et on récupère une Future.

Pb, on ne peut interagir avec une Future qu'avec la méthode get(). La composition de plusieurs futures est compliqué.

C'est à cause de ces limitations qu'est apparu la classe **CompletableFuture**.
Elle va entre autre permettre d'enregistrer des callbacks (un peu comme les promesses en javascript).
Mais on va également pouvoir chainer des traitemnts asynchrones


02 - Création

Création via la méthode statique : CompletableFuture.**supplyAsync** (2 versions!!, attention à préciser un executor service)

**supplyAsync** prend en paramètre un Supplier qui va être exécuté dans un thread background


03 - Utilisation de base

L'API Future fourni 5 méthodes, CompletableFuture en propose un peu plus.

On peut toujours interagir avec une CompletableFuture en appelant get()

Mais on peut aussi utiliser des callbacks avec **thenAccept**.

**thenAccept** prend en paramètre un objet de type Consumer -> on ne retourne rien

On va ainsi pouvoir enchainer un ou plusieurs traitements lorsque la future sera complété.

Pour éviter le callback hell, on va utiliser la méthode **thenApply** qui ressemble à **thenAccept**
mais va retourner une future du type retourné par le supplier passée en argument 
(contrairement à thenAccept qui ne retourne rien).

Le compilateur va inférer le type retourné par la fonction.

Par conséquent, l'appel à **thenApply** est également non bloquant tout comme la future initialement invoquée!!
On peut ainsi ajouter plusieurs traitements non bloquants les uns après les autres.

Au final on peut tout chainer et avoir l'impression d'avoir du code procédural non bloquant!


04 - Utilisation avancées

Avec **thenApply**, le contenu du traitement exécuté est encapsulé dans une future.

Problème, comment faire pour chainer des traitements retournant CompletableFuture
=> Problème de double wrapping

On va utiliser **thenCompose**, qui va désencapsuler 


05 - Retour au point de départ

On va maintenant voir comment exécuter en parallèle deux tâches et attendre que les deux soient complétés
pour exécuter un traitement combinant le résultat des deux traitements.

**thenCombine** pour attendre les deux tâches

**applyToEither** pour attendre une seule des deux tâches


06 - Multiplions les traitements

Avec toutes ces méthodes on peut commencer à scaler nos traitements et multiplier les CompletableFuture


08 - Gestion des exceptions

Lorsqu'on utilise thenApply, si une erreur survient, le code n'est tout simplement pas appelé!

Si on appel get() on obtient pas l'exception initial, mais une exception d'exécution qui va contenir l'exception
initiale.
Dans un monde réactif, comme on ne va jamais appeler get() explicitement, cela veut dire que l'exception
initiale va être entièrement avalé et on n'en verra pas de trace.

Heureusement, la classe CompletableFuture nous propose la méthode **handle** qui donne accès à 2 valeurs mutuellement
exclusive qui vont être :
- le résultat de l'opération si celle-ci s'est déroulé avec succès 
- ou bien l'exception.
Cela permet à minima de fournir une valeur fallback dans le cas ou une exception survient.
Cette méthode permet d'avoir accès à une exception qui a pu être levée depuis une opération antérieur (upstream)