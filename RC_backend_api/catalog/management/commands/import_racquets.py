import csv
from django.core.management.base import BaseCommand
from catalog.models import Racquet, Brand


class Command(BaseCommand):   
    help = "Import racquets from a CSV file"

    def add_arguments(self, parser):
        parser.add_argument("csv_file", type=str)  

    def handle(self, *args, **options):
        path = options["csv_file"]

        with open(path, newline="", encoding="utf-8") as f:
            reader = csv.DictReader(f)

            for row in reader:
                brand_name = row["brand_name"].strip()
                brand, _ = Brand.objects.get_or_create(brand_name=brand_name)

                Racquet.objects.create(
                    brand=brand,
                    model_name=row["model_name"],
                    model_year=int(row["model_year"]),
                    head_size_in2=int(row["head_size_in2"]),
                    length_in=float(row["length_in"]),
                    unstrung_weight_g=int(row["unstrung_weight_g"]),
                    strung_weight_g=int(row["strung_weight_g"]),
                    swing_weight=int(row["swing_weight"]),
                    twist_weight=float(row["twist_weight"]),
                    balance_mm=int(row["balance_mm"]),
                    mains=int(row["mains"]),
                    crosses=int(row["crosses"]),
                )

        self.stdout.write(self.style.SUCCESS("Import complete!"))
