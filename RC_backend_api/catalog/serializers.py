from rest_framework import serializers
from .models import Racquet, Brand

class RacquetSerializer(serializers.ModelSerializer):
    brand_name = serializers.CharField(source='brand.brand_name', read_only=True)
    brand = serializers.PrimaryKeyRelatedField(
        queryset=Brand.objects.all(),
        write_only=True
    )

    class Meta:
        model = Racquet
        fields = [
            'id',
            'model_name',
            'brand_name',
            'model_year',
            'head_size_in2',
            'length_in',
            'unstrung_weight_g',
            'strung_weight_g',
            'swing_weight',
            'twist_weight',
            'balance_mm',
            'mains',
            'crosses',
            'brand',  
        ]
        read_only_fields = ['id', 'brand_name']

class BrandSerializer(serializers.ModelSerializer):
    class Meta:
        model = Brand
        fields = ["id", "brand_name"]