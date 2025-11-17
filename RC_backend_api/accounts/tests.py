from django.test import TestCase
from rest_framework.test import APIClient
from rest_framework import status
from .models import User


class AccountAPITests(TestCase):
    def setUp(self):
        self.client = APIClient()

        self.user1 = User.objects.create_user(username='amicke3000',password="Tennis123")
        self.user2 = User.objects.create_user(username='cfile4000',password="Tennis321")

        #------------------User Account Tests------------------

    def test_login(self):
        response = self.client.post(
            '/api/accounts/login/',
            {
                'username':self.user1.username,
                'password':'Tennis123'
            }
        )
        self.assertEqual(response.status_code, status.HTTP_200_OK)
    
    def test_register(self):
        response = self.client.post(
            '/api/accounts/register/',
            {
                'username':'testUsername',
                'password':'testPassword!',
                'email': 'amicke3001@gmail.com',
                'first_name':'Avery',
                'last_name':'Mickens'
            }
        )
        self.assertEqual(response.status_code,status.HTTP_201_CREATED)

