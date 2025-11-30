from django.shortcuts import render

# Create your views here.
from rest_framework import generics
from django.contrib.auth.models import User
from .serializers import UserSerializer
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework_simplejwt.views import TokenObtainPairView, TokenRefreshView
from rest_framework.views import APIView
from rest_framework.response import Response
from .models import RacquetCollection
from .serializers import RacquetCollectionSerializer


class UserRegisterView(generics.CreateAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer
    permission_classes = [AllowAny]



login_view = TokenObtainPairView.as_view()
refresh_view = TokenRefreshView.as_view()


class CurrentUserView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        serializer = UserSerializer(request.user)
        return Response(serializer.data)


class UserRacquetCollectionView(generics.ListCreateAPIView):
    serializer_class = RacquetCollectionSerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        # return only this user's collection
        return RacquetCollection.objects.filter(user=self.request.user)

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

class UserRacquetCollectionDetailView(generics.RetrieveDestroyAPIView):
    serializer_class = RacquetCollectionSerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        return RacquetCollection.objects.filter(user=self.request.user)