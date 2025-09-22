from django.db import models
import os
import uuid


class Brand(models.Model):
    brand_name = models.CharField(max_length=30)
    country = models.CharField(max_length=30)
    year_founded = models.PositiveSmallIntegerField()

    def __str__(self):
        return self.brand_name

class Racquet(models.Model):
    brand_id = models.ForeignKey(Brand, on_delete=models.CASCADE)
    model_name = models.CharField(max_length=50)
    model_year = models.PositiveSmallIntegerField()
    head_size_in2 = models.PositiveSmallIntegerField()
    length_in = models.FloatField()
    unstrung_weight_g = models.PositiveSmallIntegerField()
    strung_weight_g = models.PositiveSmallIntegerField()
    swing_weight = models.PositiveSmallIntegerField()
    twist_weight = models.PositiveSmallIntegerField()
    balance_mm = models.PositiveSmallIntegerField()
    mains = models.PositiveSmallIntegerField()
    crosses = models.PositiveSmallIntegerField()
    
    def __str__(self):
        return self.model_name + " " + self.model_year
    
def racquet_image_upload_to(instance, filename):

    #split filename into name and extension
    base, ext = os.path.splitext(filename)

    #handle if image is uploaded without an extention
    ext = (ext or ".jpg").lower()

    #put saved file into a folder named after the primary key of the racquet the image refers to
    bucket = instance.racquet_id or "unassigned"

    return f"racquets/{bucket}/{uuid.uuid4().hex}{ext}"

class RacquetImage(models.Model):
    racquet = models.ForeignKey("catalog.Racquet",on_delete=models.CASCADE,related_name="images")
    img_file = models.ImageField(upload_to=racquet_image_upload_to)
    created_at = models.DateTimeField(auto_now_add=True)


    class Meta:
        ordering = ["-created_at", "id"]

        def __str__(self):
            return f"Racquet {self.racquet_id} image {self.pk}"
    

