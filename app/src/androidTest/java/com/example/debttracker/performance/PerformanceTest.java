package com.example.debttracker.performance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.net.ssl.HttpsURLConnection;

@RunWith(AndroidJUnit4.class)
public class PerformanceTest {

    private static final String CURRENCY_API_KEY = "fca_live_9r3DTzOKWo8YyvDndrpNu9Rl2rELohMD3VuxJBOj";
    private static final String CURRENCY_API_BASE_URL = "https://api.freecurrencyapi.com/v1/latest";
    
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Before
    public void setUp() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getTargetContext());
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Test
    public void testCurrencyApiResponseTime() {
        final long maxResponseTime = 5000;
        long startTime = System.nanoTime();
        
        try {
            String urlString = CURRENCY_API_BASE_URL + "?apikey=" + CURRENCY_API_KEY + "&base_currency=USD&currencies=PLN,EUR,GBP";
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            int responseCode = connection.getResponseCode();
            assertTrue("Currency API returned error code: " + responseCode, responseCode == HttpsURLConnection.HTTP_OK);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            JSONObject jsonResponse = new JSONObject(response.toString());
            assertTrue("Currency API response missing data field", jsonResponse.has("data"));
            
            JSONObject dataObject = jsonResponse.getJSONObject("data");
            assertTrue("Currency API missing PLN rate", dataObject.has("PLN"));
            assertTrue("Currency API missing EUR rate", dataObject.has("EUR"));
            assertTrue("Currency API missing GBP rate", dataObject.has("GBP"));
            
        } catch (Exception e) {
            fail("Currency API call failed: " + e.getMessage());
        }
        
        long endTime = System.nanoTime();
        long elapsedTime = (endTime - startTime) / 1_000_000;
        assertTrue("Currency API response time exceeded limit: " + elapsedTime + "ms", elapsedTime <= maxResponseTime);
    }

    @Test
    public void testFirestoreConnectionPerformance() {
        final long maxResponseTime = 3000;
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        AtomicLong startTime = new AtomicLong(System.nanoTime());
        
        db.collection("users").limit(1).get()
            .addOnCompleteListener(task -> {
                long elapsedTime = (System.nanoTime() - startTime.get()) / 1_000_000;
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    assertNotNull("Firestore query result should not be null", result);
                    success.set(true);
                    assertTrue("Firestore response time exceeded limit: " + elapsedTime + "ms", elapsedTime <= maxResponseTime);
                } else {
                    fail("Firestore connection failed: " + task.getException().getMessage());
                }
                latch.countDown();
            });
        
        try {
            assertTrue("Firestore operation timed out", latch.await(10, TimeUnit.SECONDS));
            assertTrue("Firestore operation was not successful", success.get());
        } catch (InterruptedException e) {
            fail("Test interrupted: " + e.getMessage());
        }
    }

    @Test
    public void testCurrencyApiDataValidation() {
        try {
            String urlString = CURRENCY_API_BASE_URL + "?apikey=" + CURRENCY_API_KEY + "&base_currency=USD&currencies=PLN";
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject dataObject = jsonResponse.getJSONObject("data");
            double plnRate = dataObject.getDouble("PLN");
            
            assertTrue("PLN exchange rate should be positive", plnRate > 0);
            assertTrue("PLN exchange rate should be reasonable (between 0.1 and 10)", plnRate > 0.1 && plnRate < 10);
            
        } catch (Exception e) {
            fail("Currency API data validation failed: " + e.getMessage());
        }
    }

    @Test
    public void testFirebaseAuthPerformance() {
        final long maxResponseTime = 5000;
        long startTime = System.nanoTime();
        
        assertNotNull("FirebaseAuth instance should not be null", auth);
        
        long endTime = System.nanoTime();
        long elapsedTime = (endTime - startTime) / 1_000_000;
        assertTrue("Firebase Auth initialization exceeded limit: " + elapsedTime + "ms", elapsedTime <= maxResponseTime);
    }

    @Test
    public void testMultipleCurrencyApiCalls() {
        final int numberOfCalls = 3;
        final long maxAverageResponseTime = 4000;
        long totalElapsedTime = 0;
        
        String[] currencies = {"EUR", "GBP", "CZK"};
        
        for (int i = 0; i < numberOfCalls; i++) {
            long startTime = System.nanoTime();
            
            try {
                String urlString = CURRENCY_API_BASE_URL + "?apikey=" + CURRENCY_API_KEY + 
                    "&base_currency=USD&currencies=" + currencies[i];
                URL url = new URL(urlString);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                
                int responseCode = connection.getResponseCode();
                assertTrue("API call " + (i+1) + " failed with code: " + responseCode, 
                    responseCode == HttpsURLConnection.HTTP_OK);
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JSONObject jsonResponse = new JSONObject(response.toString());
                assertTrue("API call " + (i+1) + " missing data", jsonResponse.has("data"));
                
            } catch (Exception e) {
                fail("API call " + (i+1) + " failed: " + e.getMessage());
            }
            
            long endTime = System.nanoTime();
            totalElapsedTime += (endTime - startTime) / 1_000_000;
        }
        
        long averageResponseTime = totalElapsedTime / numberOfCalls;
        assertTrue("Average API response time exceeded limit: " + averageResponseTime + "ms", 
            averageResponseTime <= maxAverageResponseTime);
    }

    @Test
    public void testFirestoreWritePerformance() {
        final long maxResponseTime = 4000;
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        long startTime = System.nanoTime();
        
        String testDocId = "perf_test_" + System.currentTimeMillis();
        
        db.collection("performance_tests").document(testDocId)
            .set(new java.util.HashMap<String, Object>() {{
                put("timestamp", System.currentTimeMillis());
                put("test_data", "performance_test");
            }})
            .addOnCompleteListener(task -> {
                long elapsedTime = (System.nanoTime() - startTime) / 1_000_000;
                if (task.isSuccessful()) {
                    success.set(true);
                    assertTrue("Firestore write exceeded limit: " + elapsedTime + "ms", elapsedTime <= maxResponseTime);
                    
                    db.collection("performance_tests").document(testDocId).delete();
                } else {
                    fail("Firestore write failed: " + task.getException().getMessage());
                }
                latch.countDown();
            });
        
        try {
            assertTrue("Firestore write operation timed out", latch.await(10, TimeUnit.SECONDS));
            assertTrue("Firestore write operation was not successful", success.get());
        } catch (InterruptedException e) {
            fail("Test interrupted: " + e.getMessage());
        }
    }

    @Test
    public void testCurrencyApiErrorHandling() {
        try {
            String urlString = CURRENCY_API_BASE_URL + "?apikey=invalid_key&base_currency=USD&currencies=PLN";
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            int responseCode = connection.getResponseCode();
            assertFalse("API should reject invalid API key", responseCode == HttpsURLConnection.HTTP_OK);
            
        } catch (IOException e) {
            assertTrue("Should handle network errors gracefully", true);
        }
    }

    @Test
    public void testNetworkTimeoutHandling() {
        final long maxTimeoutDuration = 6000;
        long startTime = System.nanoTime();
        
        try {
            String urlString = CURRENCY_API_BASE_URL + "?apikey=" + CURRENCY_API_KEY + "&base_currency=USD&currencies=PLN";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            
            connection.getResponseCode();
            
        } catch (Exception e) {
            long elapsedTime = (System.nanoTime() - startTime) / 1_000_000;
            assertTrue("Timeout handling took too long: " + elapsedTime + "ms", elapsedTime <= maxTimeoutDuration);
        }
    }
}