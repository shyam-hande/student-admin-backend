#!/bin/bash

# Base URL
URL="http://localhost:8080/api"

echo "Waiting for application to start..."
until curl -s http://localhost:8080/api/auth/login > /dev/null; do
    echo "Waiting for 8080..."
    sleep 2
done
echo "Application started."

# 1. Login as Default Admin
echo "1. Login as Admin..."
ADMIN_TOKEN=$(curl -s -X POST $URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin", "password":"admin123"}' | jq -r '.accessToken')

if [ "$ADMIN_TOKEN" == "null" ] || [ -z "$ADMIN_TOKEN" ]; then
    echo "Admin Login Failed"
    exit 1
fi
echo "Admin Token received."

# 2. Create Student
echo "2. Create Student (using Admin Token)..."
curl -s -X POST $URL/admin/create-student \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"username":"student1", "password":"password"}'
echo ""

# 3. Login as Student
echo "3. Login as Student..."
STUDENT_TOKEN=$(curl -s -X POST $URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"student1", "password":"password"}' | jq -r '.accessToken')

if [ "$STUDENT_TOKEN" == "null" ] || [ -z "$STUDENT_TOKEN" ]; then
    echo "Student Login Failed"
    exit 1
fi
echo "Student Token received."

# 4. Access Student Dashboard
echo "4. Access Student Dashboard..."
STATUS=$(curl -s -o /dev/null -w "%{http_code}" -H "Authorization: Bearer $STUDENT_TOKEN" $URL/student/dashboard)
if [ "$STATUS" == "200" ]; then
    echo "Student Dashboard Access: SUCCESS"
else
    echo "Student Dashboard Access: FAILED (Status: $STATUS)"
fi

# 5. Verify Student cannot create admin (Admin Endpoint)
echo "5. Verify Student cannot access Admin Endpoint..."
STATUS=$(curl -s -o /dev/null -w "%{http_code}" -H "Authorization: Bearer $STUDENT_TOKEN" -X POST $URL/admin/create-admin \
    -H "Content-Type: application/json" \
    -d '{"username":"admin2", "password":"password"}')

if [ "$STATUS" == "403" ]; then
    echo "Access Denied Correctly: SUCCESS (Status: 403)"
else
    echo "Access Denied Check: FAILED (Status: $STATUS)"
fi

echo "Verification Complete."
