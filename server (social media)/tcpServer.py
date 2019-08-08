import socket
import json
from threading import Thread
import exception


class TcpServer(Thread):
    def __init__(self, port, server):
        Thread.__init__(self)
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.port = port
        self.sock.bind(('0.0.0.0', self.port))
        print(socket.gethostbyname(socket.gethostname()), self.port)
        self.sock.setblocking(True)
        self.sock.settimeout(2)
        self.sock.listen(1)
        self.is_listening = True
        self.server = server

    def run(self):
        while self.is_listening:
            try:
                conn, addr = self.sock.accept()
            except socket.timeout:
                pass
            else:
                data = conn.recvfrom(1024)
                try:
                    data = json.loads(data[0])
                except UnicodeDecodeError:
                    ans = {'result': 'NotJson'}
                else:
                    ans = self.parse_data(data, addr)
                conn.sendall(json.dumps(ans).encode())
                conn.close()

    def parse_data(self, data, addr):
        print(data, addr)
        if not isinstance(data, dict):
            return None
        try:
            action = data['action']
        except KeyError:
            return None

        if action == 'register':
            try:
                username = data['username']
            except KeyError:
                return None

            try:
                password = data['password']
            except KeyError:
                return None

            try:
                self.server.register(username, password)
            except exception.UsedUsername:
                ans = {'result': 'UsedUsername'}
            else:
                ans = {'result': 'ok'}

            return ans

        if action == 'login':
            try:
                username = data['username']
            except KeyError:
                return None

            try:
                password = data['password']
            except KeyError:
                return None

            try:
                self.server.login(username, password, addr[0])
            except exception.IncorrectLogin:
                ans = {'result': 'IncorrectLogin'}
            else:
                ans = {'result': 'ok'}

            return ans

        if action == 'like':
            try:
                post_id = data['post_id']
            except KeyError:
                return None
            try:
                self.server.like(int(post_id), addr[0])
            except exception.NotLoggedIn:
                ans = {'result': 'NotLoggedIn'}
            except exception.AlreadyLiked:
                ans = {'result': 'AlreadyLiked'}
            except exception.NonExistingPost:
                ans = {'result': 'NonExistingPost'}
            except exception.NonExistingUser:
                ans = {'result': 'NonExistingUser'}
            else:
                ans = {'result': 'ok'}

            return ans

        if action == 'send_friend_request':
            try:
                target_id = data['target_id']
            except KeyError:
                return None

            try:
                self.server.send_friend_request(addr[0], int(target_id))
            except exception.NonExistingUser:
                ans = {'result': 'NonExistingUser'}
            except exception.NotLoggedIn:
                ans = {'result': 'NotLoggedIn'}
            except exception.FriendRequestAlreadySent:
                ans = {'result': 'FriendRequestAlreadySent'}
            except exception.AlreadyFriend:
                ans = {'result': 'AlreadyFriend'}
            else:
                ans = {'result': 'ok'}

            return ans

        if action == 'accept_friend_request':
            try:
                fr_index = data['fr_index']
            except KeyError:
                return None

            try:
                self.server.accept_friend_request(addr[0], int(fr_index))
            except exception.NotLoggedIn:
                ans = {'result': 'NotLoggedIn'}
            except exception.NonExistingFriendRequest:
                ans = {'result': 'NonExistingFriendRequest'}
            else:
                ans = {'result': 'ok'}

            return ans

        if action == 'deny_friend_request':
            try:
                fr_index = data['fr_index']
            except KeyError:
                return None

            try:
                self.server.deny_friend_request(addr[0], int(fr_index))
            except exception.NotLoggedIn:
                ans = {'result': 'NotLoggedIn'}
            except exception.NonExistingFriendRequest:
                ans = {'result': 'NonExistingFriendRequest'}
            else:
                ans = {'result': 'ok'}

            return ans

        if action == 'post':
            try:
                caption = data['caption']
            except KeyError:
                return None

            try:
                file_format = data['format']
            except KeyError:
                return None

            try:
                ans_sock = self.server.begin_post(caption, file_format, addr)
            except exception.NotLoggedIn:
                ans = {'result': 'NotLoggedIn'}
            except exception.NotSupportedFormat:
                ans = {'result': 'NotSupportedFormat'}
            else:
                # we only need the port not the ip
                ans = {'result': 'ok', 'port': ans_sock[1]}

            return ans

        if action == 'request_post':
            try:
                number_in_line = data['number']
            except KeyError:
                return None

            try:
                ans_sock = self.server.request_post(int(number_in_line), addr)
            except exception.NotLoggedIn:
                ans = {'result': 'NotLoggedIn'}
            except exception.NonExistingPost:
                ans = {'result': 'NonExistingPost'}
            else:
                # we only need the port not the ip
                ans = {'result': 'ok', 'port': ans_sock[1]}

            return ans

        if action == 'number_of_posts':
            try:
                number_of_posts = self.server.number_of_posts(addr[0])
                print(number_of_posts)
            except exception.NotLoggedIn:
                ans = {'result': 'NotLoggedIn'}
            else:
                ans = {'result': 'ok', 'numberOfPosts': number_of_posts}

            return ans

        if action == 'number_of_friend_requests':
            try:
                number_of_fr = self.server.number_of_friend_requests(addr[0])
            except exception.NotLoggedIn:
                ans = {'result': 'NotLoggedIn'}
            else:
                ans = {'result': 'ok', 'numberOfFR': number_of_fr}

            return ans

        if action == 'request_friend_request':
            try:
                position = data['number']
            except KeyError:
                return None

            try:
                sender = self.server.get_fr(position, addr[0])
            except exception.NonExistingFriendRequest:
                ans = {'result': 'NonExistingFriendRequest'}
            else:
                ans = {'result': 'ok', 'sender': sender.username}

            return ans
