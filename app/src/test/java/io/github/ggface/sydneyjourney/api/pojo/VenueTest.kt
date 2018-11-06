package io.github.ggface.sydneyjourney.api.pojo

import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Testing [Venue]
 *
 * @author Ivan Novikov on 2018-11-06.
 */
class VenueTest {

    private val mDefaultName = "Place"
    private val mDefaultLat = 10.12
    private val mDefaultLon = -33.10
    private val mDefaultDescription = "My favorite place"
    private val mDefaultIsManual = false

    private lateinit var mVenue: Venue

    @Before
    fun setUp() {
        mVenue = Venue(mDefaultName, mDefaultLat, mDefaultLon, mDefaultDescription, mDefaultIsManual)
    }

    //region test object's methods
    @Test
    fun testToString() {
        Assert.assertNotNull(mVenue.toString())
        val another = Venue(mDefaultName, mDefaultLat, mDefaultLon, null, mDefaultIsManual)
        Assert.assertNotNull(another)
        assertNotEquals(mVenue.toString(), another.toString())
    }

    @Test
    fun testEquals() {
        val another = Venue(mDefaultName, mDefaultLat, mDefaultLon, mDefaultDescription, mDefaultIsManual)
        assertEquals(mVenue, another)
        assertEquals(mVenue.hashCode(), another.hashCode())
        assertEquals(mVenue, mVenue)
    }

    @Test
    fun testNotEquals() {
        assertFalse(mVenue.equals(null))
        assertFalse(mVenue.equals(Any()))
        assertFalse(mVenue.equals(Venue("", 0.0, 0.0, null, false)))

        // different name
        var another = Venue("New York", mDefaultLat, mDefaultLon, mDefaultDescription, mDefaultIsManual)
        assertNotEquals(another, mVenue)

        // different latitude
        another = Venue(mDefaultName, 1.0, mDefaultLat, mDefaultDescription, mDefaultIsManual)
        assertNotEquals(another, mVenue)

        // different longitude
        another = Venue(mDefaultName, mDefaultLat, 2.0, mDefaultDescription, mDefaultIsManual)
        assertNotEquals(another, mVenue)

        // different description
        another = Venue(mDefaultName, mDefaultLat, mDefaultLon, "Awful place", mDefaultIsManual)
        assertNotEquals(another, mVenue)

        // different isManual
        another = Venue(mDefaultName, mDefaultLat, mDefaultLon, mDefaultDescription, !mDefaultIsManual)
        assertNotEquals(another, mVenue)
    }
    //endregion test object's methods

    //region test getters
    @Test
    fun testGetName() {
        assertEquals(mVenue.name, mDefaultName)
    }

    @Test
    fun testGetLatitude() {
        assertEquals(mVenue.latitude, mDefaultLat, 0.0)
    }

    @Test
    fun testGetLongitude() {
        assertEquals(mVenue.longitude, mDefaultLon, 0.0)
    }

    @Test
    fun testGetDescription() {
        assertEquals(mVenue.description, mDefaultDescription)
    }

    @Test
    fun testIsManual() {
        assertEquals(mVenue.isManual, mDefaultIsManual)
    }
    //endregion test getters
}