from django.db.models import Q
from rest_framework.viewsets import ReadOnlyModelViewSet
from rest_framework.permissions import IsAuthenticatedOrReadOnly
from rest_framework.filters import SearchFilter, OrderingFilter
from rest_framework.pagination import PageNumberPagination
from django_filters.rest_framework import DjangoFilterBackend, FilterSet
import django_filters as filters
from rest_framework.decorators import action
from rest_framework.response import Response

from .models import Racquet
from .serializers import RacquetSerializer
from .models import Brand

from .serializers import BrandSerializer

class DefaultPagination(PageNumberPagination):
    page_size = 25
    page_size_query_param = "page_size"
    max_page_size = 200


class RacquetFilter(FilterSet):
    brand = filters.NumberFilter(field_name="brand")
    model_year = filters.NumberFilter(field_name="model_year")
    brand_name = filters.CharFilter(field_name="brand__brand_name", lookup_expr="icontains")
    name = filters.CharFilter(field_name="model_name", lookup_expr="icontains")

    year_min = filters.NumberFilter(field_name="model_year", lookup_expr="gte")
    year_max = filters.NumberFilter(field_name="model_year", lookup_expr="lte")
    head_min = filters.NumberFilter(field_name="head_size_in2", lookup_expr="gte")
    head_max = filters.NumberFilter(field_name="head_size_in2", lookup_expr="lte")
    length_min = filters.NumberFilter(field_name="length_in", lookup_expr="gte")
    length_max = filters.NumberFilter(field_name="length_in", lookup_expr="lte")
    unstrung_min = filters.NumberFilter(field_name="unstrung_weight_g", lookup_expr="gte")
    unstrung_max = filters.NumberFilter(field_name="unstrung_weight_g", lookup_expr="lte")
    strung_min = filters.NumberFilter(field_name="strung_weight_g", lookup_expr="gte")
    strung_max = filters.NumberFilter(field_name="strung_weight_g", lookup_expr="lte")
    swing_min = filters.NumberFilter(field_name="swing_weight", lookup_expr="gte")
    swing_max = filters.NumberFilter(field_name="swing_weight", lookup_expr="lte")
    twist_min = filters.NumberFilter(field_name="twist_weight", lookup_expr="gte")
    twist_max = filters.NumberFilter(field_name="twist_weight", lookup_expr="lte")
    balance_min = filters.NumberFilter(field_name="balance_mm", lookup_expr="gte")
    balance_max = filters.NumberFilter(field_name="balance_mm", lookup_expr="lte")

    class Meta:
        model = Racquet
        fields = []


class RacquetViewSet(ReadOnlyModelViewSet):
    """GET list/detail with fast filtering/search/sort."""
    permission_classes = [IsAuthenticatedOrReadOnly]
    serializer_class = RacquetSerializer
    pagination_class = DefaultPagination
    queryset = Racquet.objects.select_related("brand")

    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_class = RacquetFilter
    search_fields = ["model_name", "brand__brand_name"]
    ordering_fields = [
        "model_year", "head_size_in2", "length_in",
        "unstrung_weight_g", "strung_weight_g",
        "swing_weight", "twist_weight", "balance_mm",
    ]
    ordering = ["-model_year"]

    @action(detail=False, url_path="search")
    def search(self, request):
        q = request.query_params.get("q")
        queryset = self.get_queryset()
        if q:
            queryset = queryset.filter(
                Q(model_name__icontains=q) |
                Q(brand__brand_name__icontains=q)  # Correct FK lookup
            )
        serializer = self.get_serializer(queryset, many=True)
        return Response(serializer.data)


class BrandViewSet(ReadOnlyModelViewSet):
    queryset = Brand.objects.all()
    serializer_class = BrandSerializer
