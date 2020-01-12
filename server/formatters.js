const formatUser =
    module.exports.user =
    ({ name }) => ({
        name
    });

const formatCurrentUser =
    module.exports.currentUser =
    ({ token, lobby, ...user }) => ({
        token,
        lobby: lobby && formatLobby(lobby),
        ...formatUser(user)
    });

const formatLobby =
    module.exports.lobby =
    ({ name, users }) => ({
        name,
        users: users.map(formatUser)
    });

const formatMessage =
    module.exports.message =
    ({ sender: { name }, body }) => ({
        sender: name,
        body
    });
