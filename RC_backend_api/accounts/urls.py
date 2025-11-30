from django.urls import path
from accounts.views import UserRegisterView, login_view, refresh_view, CurrentUserView
from .views import UserRacquetCollectionView, UserRacquetCollectionDetailView

urlpatterns = [
    path('register/', UserRegisterView.as_view(), name='user-register'),
    path('login/', login_view, name='login'),
    path('profile/',CurrentUserView.as_view(),name='profile'),
    path('collection/', UserRacquetCollectionView.as_view(), name='user-collection'),
    path('collection/<int:pk>/', UserRacquetCollectionDetailView.as_view(), name='user-collection-detail'),
    path('token/refresh/', refresh_view, name='token_refresh'),
]
