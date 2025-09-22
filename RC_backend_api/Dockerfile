#Pull official base image
FROM python:3.11

#Set environment variables
ENV PYTHONDONTWRITEBYTECODE=1

ENV PYTHONBUFFERED=1

# Install different linux packages

RUN apt-get update \
&& apt-get install gcc postgresql postgresql-contrib libpq-dev python3-dev netcat-openbsd -y \
&& apt-get clean

# set working directory
WORKDIR /app

COPY ./requirements ./requirements
RUN pip install -r requirements/requirements-base.txt

COPY . /app/

COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN sed -i 's/\r$//' /usr/local/bin/entrypoint.sh && chmod +x /usr/local/bin/entrypoint.sh

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
CMD ["python", "manage.py", "runserver", "0.0.0.0:8000"]


