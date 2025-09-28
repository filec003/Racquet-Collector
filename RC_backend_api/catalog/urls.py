from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import RacquetViewSet  
router = DefaultRouter()
router.register(r"racquets", RacquetViewSet, basename="racquet")

urlpatterns = [
    path("", include(router.urls)),
]
