package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Janmanch India Tea", appName)
  }

  @Test
  fun `create post model verification`() {
    val post = com.example.model.PostEntity(
      id = "test_post_1",
      authorId = "user_me",
      authorName = "Raju Meena",
      authorUsername = "raju_meena",
      authorAvatarUrl = "",
      category = "चाय और चर्चा",
      content = "चाय पर सार्थक चर्चा",
      chaiMood = "☕ कड़क मसाला चाय"
    )
    assertEquals("test_post_1", post.id)
    assertEquals("चाय और चर्चा", post.category)
    assertEquals("चाय पर सार्थक चर्चा", post.content)
  }
}
