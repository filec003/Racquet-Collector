from rest_framework import serializers
from .models import Racquet,Brand

class RacquetSerializer(serializers.ModelSerializer):
    brand_name = serializers.CharField(source='brand.name', read_only=True)
    brand = serializers.PrimaryKeyRelatedField(
        source='brand',
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
            'strung_weight',
            'swing_weight',
            'twist_weight',
            'balance_mm',
            'mains',
            'crosses',
            ]
        read_only_fields = ['id', 'brand_name']
