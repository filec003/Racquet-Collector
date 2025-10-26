from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import RacquetViewSet ,BrandViewSet

router = DefaultRouter()
router.register(r"racquets", RacquetViewSet, basename="racquet")
router.register(r'brands',BrandViewSet,basename='brand')
urlpatterns = [
    path("", include(router.urls)),
]
