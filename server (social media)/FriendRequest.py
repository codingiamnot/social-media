class FriendRequest:
    def __init__(self, origin, target):
        self.origin = origin
        self.target = target

    def accept(self):
        self.origin.friends.append(self.target)
        self.target.friends.append(self.origin)
        self.target.friend_requests.remove(self)

    def deny(self):
        self.target.friend_requests.remove(self)
