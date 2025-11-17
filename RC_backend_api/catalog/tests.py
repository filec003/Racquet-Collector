from django.test import TestCase
from rest_framework.test import APIClient
from rest_framework import status
from .models import Racquet, Brand
from django.urls import reverse

class RacquetAPITests(TestCase):
    def setUp(self):
        self.client = APIClient()

        # Create brands with all required fields
        self.brand1 = Brand.objects.create(
            brand_name="Prince",
            year_founded=1970
        )
        self.brand2 = Brand.objects.create(
            brand_name="Wilson",
            year_founded=1914
        )

        # Create racquets
        self.racquet1 = Racquet.objects.create(
            model_name="Phantom 1",
            brand=self.brand1,
            model_year=2023,
            head_size_in2=100,
            length_in=27,
            unstrung_weight_g=300,
            strung_weight_g=320,
            swing_weight=320,
            twist_weight=14,
            balance_mm=320,
            mains=16,
            crosses=19,
        )

        self.racquet2 = Racquet.objects.create(
            model_name="Phantom 2",
            brand=self.brand2,
            model_year=2022,
            head_size_in2=98,
            length_in=27,
            unstrung_weight_g=305,
            strung_weight_g=325,
            swing_weight=315,
            twist_weight=13,
            balance_mm=315,
            mains=16,
            crosses=19,
        )

    # ---------------- Racquet Tests ----------------
    def test_list_racquets(self):
        """Test listing all racquets."""
        response = self.client.get("/api/racquets/")
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        # Handle pagination
        results = response.data.get('results', response.data)
        self.assertEqual(len(results), 2)
    
    def test_get_racquets_by_brand_name(self):
        url = reverse('racquet-list')
        response = self.client.get(url,{'brand_name':'Wilson'})
        self.assertEqual(response.status_code, 200)
        self.assertEqual(len(response.data(['results'])),1)

    def test_retrieve_racquet(self):
        """Test retrieving a single racquet by ID."""
        response = self.client.get(f"/api/racquets/{self.racquet1.id}/")
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data["model_name"], self.racquet1.model_name)

    def test_create_racquet_unauthenticated(self):
        """Test that creating a racquet without authentication fails."""
        data = {
            "model_name": "Phantom 3",
            "brand": self.brand1.id,
            "model_year": 2025,
            "head_size_in2": 102,
            "length_in": 27,
            "unstrung_weight_g": 310,
            "strung_weight_g": 330,
            "swing_weight": 325,
            "twist_weight": 15,
            "balance_mm": 325,
            "mains": 16,
            "crosses": 19,
        }
        response = self.client.post("/api/racquets/", data)
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)

    def test_search_racquet(self):
        """Test custom search action."""
        response = self.client.get("/api/racquets/search/?q=Phantom 1")
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 1)
        self.assertEqual(response.data[0]["model_name"], "Phantom 1")

    # ---------------- Brand Tests ----------------
    def test_list_brands(self):
        """Test listing all brands in the database."""
        response = self.client.get("/api/brands/")
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        # Handle pagination if applied
        brands = response.data.get('results', response.data)
        self.assertEqual(len(brands), 2)

        # Check that all brands created in setUp are returned
        brand_names = [brand['brand_name'] for brand in brands]
        self.assertIn("Prince", brand_names)
        self.assertIn("Wilson", brand_names)
