from constant import large_prime_number as lpn
from Listener import Listener


class Post:
    def __init__(self, op, caption, post_id, file_format, addr):
        self.likes = 0
        self.op = op
        self.caption = caption
        self.id = post_id
        self.liked_by = []
        self.file_format = file_format
        self.link = 'users/' + str(int(post_id / lpn)) + '/' + str(post_id % lpn) + '.' + self.file_format
        self.list_sock = None
        listener = Listener(self.link, addr, self)
        listener.start()

    def send_to_friends(self):
        for friend in self.op.friends:
            friend.to_view.append(self)
