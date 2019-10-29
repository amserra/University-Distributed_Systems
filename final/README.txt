Para executar existem 3 ficheiros .jar:

1. dataserver.jar
2. client.jar
3. server.jar

Deve colocá-los todos no mesmo diretório para ser mais fácil o processo de execução.

A título de exemplo, vamos usar o diretório /Desktop. Deverá então colocar os 3 ficheiros em /Desktop

1. Crie uma pasta em /Desktop chamada files. Deverá existir um diretório /Desktop/files/

2. Se ainda não estiver, faça cd para o diretório /Desktop e execute cada um dos ficheiros da seguinte forma:


java -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8 -cp server.jar RMIServer [Args: opcional]


java -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8 -cp dataserver.jar MulticastServer [Args: obrigatório]


java -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8 -cp client.jar RMIClient [Args: opcional]

NOTA: Os argumentos são o IP e Porto RMI para o RMIServer e RMIClient e o IP e Porto de rede para o MulticastServer.

A ordem preferencial de abertura de janelas do terminal é:

1. server.jar (servidor rmi primário)
2. server.jar (servidor rmi secundário)
3. dataserver.jar (multicast server)
4. clients, dataservers, etc


