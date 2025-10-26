from django.shortcuts import render

# Create your views here.
from rest_framework import generics
from django.contrib.auth.models import User
from .serializers import UserSerializer
from rest_framework.permissions import AllowAny

class UserRegisterView(generics.CreateAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer
    permission_classes = [AllowAny]


from rest_framework_simplejwt.views import TokenObtainPairView, TokenRefreshView

login_view = TokenObtainPairView.as_view()
refresh_view = TokenRefreshView.as_view()
