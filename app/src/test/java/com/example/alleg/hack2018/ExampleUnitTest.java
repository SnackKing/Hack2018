//package com.example.alleg.hack2018;
//
//import android.content.Context;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.filters.MediumTest;
//import android.support.test.runner.AndroidJUnit4;
//
//import com.example.alleg.hack2018.utility.DBUtility;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.HashMap;
//
//import static org.junit.Assert.*;
//
///**
// * Example local unit test, which will execute on the development machine (host).
// *
// * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
// */
//
//
//@RunWith(AndroidJUnit4.class)
//@MediumTest
//public class ExampleUnitTest {
//    private Context context;
//
//    @Before
//    public void setup() {
//        context = InstrumentationRegistry.getContext();
//    }
//
//    @Test
//    public void testJSON() {
//        DBUtility util = new DBUtility(context);
//        HashMap<String, String> jsonMap = util.dataToHashmap();
//        String json = jsonMap.get("text");
//        System.out.print(json);
//    }
//}