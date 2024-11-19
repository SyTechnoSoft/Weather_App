package com.sytechnosoft.weather

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

class SearchActivity : AppCompatActivity() {
    private lateinit var searchView: SearchView
    private lateinit var listView: ListView
    private lateinit var placesClient: PlacesClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        searchView = findViewById(R.id.searchView)
        listView = findViewById(R.id.listView)

        // Initialize the Places API
        Places.initialize(applicationContext, "86c279baf0msh390f037366033fap1da546jsn2beb724afbf")
        placesClient = Places.createClient(this)

        setupSearchView()
    }

    private fun setupSearchView() {
        val suggestions = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, suggestions)
        listView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.isNotEmpty()) {
                    getSuggestions(newText, adapter, suggestions)
                } else {
                    suggestions.clear()
                    adapter.notifyDataSetChanged()
                }
                return true
            }
        })
    }

    private fun getSuggestions(query: String, adapter: ArrayAdapter<String>, suggestions: MutableList<String>) {
        val token = AutocompleteSessionToken.newInstance()

        // Create a request to fetch auto-suggestions
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setSessionToken(token)
            .build()

        // Fetch predictions
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                suggestions.clear()
                for (prediction: AutocompletePrediction in response.autocompletePredictions) {
                    suggestions.add(prediction.getFullText(null).toString())
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }
}