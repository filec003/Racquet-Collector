from django.db import models
from django.contrib.auth.models import User
from catalog.models import Racquet

class RacquetCollection(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="racquet_collection")
    racquet = models.ForeignKey(Racquet, on_delete=models.CASCADE, related_name="collected_by")
    added_on = models.DateTimeField(auto_now_add=True)
    notes = models.TextField(blank=True)  

    class Meta:
        unique_together = ('user', 'racquet') 
    def __str__(self):
        return f"{self.user.username} - {self.racquet.model_name}"