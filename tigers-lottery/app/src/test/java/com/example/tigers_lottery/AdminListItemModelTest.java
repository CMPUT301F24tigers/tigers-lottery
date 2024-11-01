package com.example.tigers_lottery;

import com.example.tigers_lottery.Admin.AdminListItemModel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AdminListItemModelTest {

    private AdminListItemModel adminListItemModel;

    @Before
    public void setUp() {
        adminListItemModel = new AdminListItemModel("Test Title", 100);
    }

    @Test
    public void testConstructor_initializesFieldsCorrectly() {
        assertNotNull(adminListItemModel);
        assertEquals("Test Title", adminListItemModel.getTitle());
        assertEquals(100, adminListItemModel.getCount());
    }

    @Test
    public void testGetTitle_returnsCorrectTitle() {
        assertEquals("Test Title", adminListItemModel.getTitle());
    }

    @Test
    public void testGetCount_returnsCorrectCount() {
        assertEquals(100, adminListItemModel.getCount());
    }
}