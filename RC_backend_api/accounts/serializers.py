from rest_framework import serializers
from django.contrib.auth.models import User
from catalog.models import Racquet  # Make sure Racquet is imported
from catalog.serializers import RacquetSerializer
from .models import RacquetCollection

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['id', 'username', 'email', 'password','first_name','last_name']
        extra_kwargs = {'password': {'write_only': True}}

    def create(self, validated_data):
        user = User.objects.create_user(
            username=validated_data['username'],
            email=validated_data.get('email'),
            password=validated_data['password'],
            first_name=validated_data.get('first_name', ''),
            last_name=validated_data.get('last_name', '')
        )

        return user

class RacquetCollectionSerializer(serializers.ModelSerializer):
    # This remains for reading the full racquet details in GET requests
    racquet = RacquetSerializer(read_only=True)
    
    # Add this field for writing. It expects a racquet ID.
    racquet_id = serializers.PrimaryKeyRelatedField(
        queryset=Racquet.objects.all(), source='racquet', write_only=True
    )

    class Meta:
        model = RacquetCollection
        # Ensure all necessary fields are included
        fields = ['id', 'racquet', 'racquet_id', 'added_on', 'notes']
        read_only_fields = ['id', 'added_on'] # notes can be edited
