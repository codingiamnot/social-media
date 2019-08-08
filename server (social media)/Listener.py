from threading import Thread
import socket


class Listener(Thread):
    def __init__(self, link, addr, post):
        Thread.__init__(self)
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.ip = socket.gethostbyname(socket.gethostname())
        self.sock.bind((self.ip, 0))
        self.sock.setblocking(True)
        self.sock.settimeout(2)
        self.sock.listen(1)
        self.link = link
        self.addr = addr
        self.post = post
        self.nr_timeouts = 0
        self.post.list_sock = (self.ip, self.sock.getsockname()[1])

    def run(self):

        print(self.link, self.addr, self.sock.getsockname())
        out_file = open(self.link, 'wb')
        while True:
            try:
                conn, adr = self.sock.accept()
                print('conn made')
            except socket.timeout:
                self.nr_timeouts += 1
                if self.nr_timeouts >= 5:
                    break
            else:
                # dumb bug where the sock is once read as 127.0.0.1 and once as real ip
                dumb_bug = (adr[0] == '127.0.0.1' and self.addr[0] == self.ip) or \
                           (self.addr[0] == '127.0.0.1' and adr[0] == self.ip)
                if adr[0] != self.addr[0] and not dumb_bug:
                    conn.close()
                    print(adr[0], self.addr[0])
                else:
                    data = conn.recv(1024)
                    while data:
                        out_file.write(data)
                        data = conn.recv(1024)
                    break

        self.post.send_to_friends()
