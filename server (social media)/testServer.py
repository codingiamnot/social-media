import socket  # Import socket module

soc = socket.socket()  # Create a socket object
host = "localhost"  # Get local machine name
port = 1234  # Reserve a port for your service.
soc.bind((host, port))  # Bind to the port
soc.settimeout(5)
soc.setblocking(True)
soc.listen(1)
while True:
    try:
        conn, addr = soc.accept()  # Establish connection with client.
    except socket.timeout:
        pass
    else:
        print("Got connection from", addr)
        msg = conn.recv(1024)
        print(msg)
        if msg == "Hello Server":
            print("Hii everyone")
        else:
            print("Go away")
