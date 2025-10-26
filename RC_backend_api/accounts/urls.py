from django.urls import path
from accounts.views import UserRegisterView, login_view, refresh_view

urlpatterns = [
    path('register/', UserRegisterView.as_view(), name='user-register'),
    path('login/', login_view, name='login'),
    path('token/refresh/', refresh_view, name='token_refresh'),
]
