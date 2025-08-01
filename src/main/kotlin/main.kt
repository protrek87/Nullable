interface Attachment {
    val type: String
}

// Photo
data class PhotoAttachment(val photo: Photo) : Attachment {
    override val type: String = "photo"
}

data class Photo(
    val id: Int,
    val ownerId: Int,
    val url: String,
    val width: Int = 0,
    val height: Int = 0
)

// Video
data class VideoAttachment(val video: Video) : Attachment {
    override val type: String = "video"
}

data class Video(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val duration: Int
)

// Audio
data class AudioAttachment(val audio: Audio) : Attachment {
    override val type: String = "audio"
}

data class Audio(
    val id: Int,
    val ownerId: Int,
    val artist: String,
    val title: String,
    val duration: Int
)

// Document
data class DocAttachment(val doc: Doc) : Attachment {
    override val type: String = "doc"
}

data class Doc(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val size: Int,
    val ext: String
)

// Link
data class LinkAttachment(val link: Link) : Attachment {
    override val type: String = "link"
}

data class Link(
    val url: String,
    val title: String
)

data class Post(
    val id: Int,
    val ownerId: Int,
    val fromId: Int,
    val date: Int,
    val text: String,
    val attachments: Array<Attachment>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (id != other.id) return false
        if (ownerId != other.ownerId) return false
        if (fromId != other.fromId) return false
        if (date != other.date) return false
        if (text != other.text) return false
        if (attachments != null) {
            if (other.attachments == null) return false
            if (!attachments.contentEquals(other.attachments)) return false
        } else if (other.attachments != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + ownerId
        result = 31 * result + fromId
        result = 31 * result + date
        result = 31 * result + text.hashCode()
        result = 31 * result + (attachments?.contentHashCode() ?: 0)
        return result
    }
}

object WallService {
    private var posts = emptyArray<Post>()
    private var nextId = 1

    fun add(post: Post): Post {
        val newPost = post.copy(id = nextId++)
        posts += newPost
        return newPost
    }

    fun update(updatedPost: Post): Boolean {
        for ((index, post) in posts.withIndex()) {
            if (post.id == updatedPost.id) {
                posts[index] = updatedPost
                return true
            }
        }
        return false
    }

    fun clear() {
        posts = emptyArray()
        nextId = 1
    }

    fun getPosts(): Array<Post> {
        return posts.copyOf()
    }
}