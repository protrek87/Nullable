import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class WallServiceTest {

    @Before
    fun clearBeforeTest() {
        WallService.clear()
    }

    @Test
    fun addNewPost_shouldAssignNonZeroId() {
        val post = Post(0, 1, 1, 1234567890, "Test post")
        val addedPost = WallService.add(post)
        assertTrue("ID should be greater than 0", addedPost.id > 0)
    }

    @Test
    fun addMultiplePosts_shouldHaveIncrementalIds() {
        val post1 = Post(0, 1, 1, 1234567890, "First post")
        val post2 = Post(0, 1, 1, 1234567891, "Second post")

        val added1 = WallService.add(post1)
        val added2 = WallService.add(post2)

        assertEquals("Second post should have ID = first post ID + 1", added1.id + 1, added2.id)
    }

    @Test
    fun updateExistingPost_shouldReturnTrue() {
        val original = Post(0, 1, 1, 1234567890, "Original")
        val added = WallService.add(original)
        val updated = added.copy(text = "Updated")

        assertTrue(WallService.update(updated))
    }

    @Test
    fun updateNonExistingPost_shouldReturnFalse() {
        val post = Post(999, 1, 1, 1234567890, "Non-existent")
        assertFalse(WallService.update(post))
    }

    @Test
    fun addPostWithSingleAttachment_shouldSaveAttachment() {
        val photo = Photo(1, 1, "photo_url", 800, 600)
        val post = Post(
            0, 1, 1, 1234567890, "Post with photo",
            attachments = arrayOf(PhotoAttachment(photo))
        )

        val addedPost = WallService.add(post)

        assertNotNull("Attachments shouldn't be null", addedPost.attachments)
        assertEquals("Should have exactly 1 attachment", 1, addedPost.attachments?.size)

        val attachment = addedPost.attachments?.firstOrNull()
        assertTrue("Attachment should be PhotoAttachment", attachment is PhotoAttachment)

        (attachment as? PhotoAttachment)?.let {
            assertEquals("photo", it.type)
            assertEquals(800, it.photo.width)
        } ?: fail("Expected PhotoAttachment")
    }

    @Test
    fun addPostWithMultipleAttachments_shouldSaveAll() {
        val attachments = arrayOf(
            PhotoAttachment(Photo(1, 1, "photo_url")),
            VideoAttachment(Video(1, 1, "Video", 120)),
            AudioAttachment(Audio(1, 1, "Artist", "Song", 180))
        )

        val post = Post(0, 1, 1, 1234567890, "Post with attachments", attachments)
        val addedPost = WallService.add(post)

        assertEquals(3, addedPost.attachments?.size)

        val videoAttachment = addedPost.attachments?.find { it.type == "video" } as? VideoAttachment
        assertNotNull("Should contain video attachment", videoAttachment)
        assertEquals("Video", videoAttachment?.video?.title)
    }

    @Test
    fun updatePostAttachments_shouldUpdateCorrectly() {
        // 1. Add original post
        val original = Post(
            0, 1, 1, 1234567890, "Original",
            attachments = arrayOf(DocAttachment(Doc(1, 1, "doc.pdf", 1024, "pdf")))
        )
        val added = WallService.add(original)

        // 2. Prepare update
        val updated = added.copy(
            text = "Updated",
            attachments = arrayOf(LinkAttachment(Link("https://example.com", "Example")))
        )

        // 3. Verify update
        assertTrue(WallService.update(updated))

        val updatedPost = WallService.getPosts().firstOrNull()
        assertNotNull(updatedPost)
        assertEquals("Updated", updatedPost?.text)

        val linkAttachment = updatedPost?.attachments?.firstOrNull() as? LinkAttachment
        assertNotNull(linkAttachment)
        assertEquals("Example", linkAttachment?.link?.title)
    }

    @Test
    fun getPosts_shouldReturnCopyNotReference() {
        val post = Post(0, 1, 1, 1234567890, "Test")
        WallService.add(post)

        val posts1 = WallService.getPosts()
        val posts2 = WallService.getPosts()

        assertNotSame("Should return different array instances", posts1, posts2)
        assertArrayEquals("Arrays should contain same elements", posts1, posts2)
    }

    @Test
    fun clear_shouldResetServiceState() {
        WallService.add(Post(0, 1, 1, 1234567890, "First"))
        WallService.add(Post(0, 1, 1, 1234567891, "Second"))

        WallService.clear()

        assertEquals("Posts should be empty after clear", 0, WallService.getPosts().size)
    }
}