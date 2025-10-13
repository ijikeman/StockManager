#!/bin/bash

# Usage:
# ./add_income.sh <lotId> <incoming> <paymentDate>
# Example:
# ./add_income.sh 1 1000.00 2025-10-09

set -euo pipefail

# if [ "$#" -ne 7 ]; then
#         echo "Usage: $0 <lotId> <incoming> <paymentDate>"
#         echo "Example: $0 1 100.00 2025-10-09"
#         exit 1
# fi

STOCKLOT_ID=$1
INCOMING=$2
PAYMENT_DATE=$3

# Allow overriding the base URL via environment variable, default to localhost
BASE_URL="${BASE_URL:-http://localhost:8080}"
API_URL="$BASE_URL/api/incominghistory"

JSON_PAYLOAD=$(cat <<EOF
{
    "lotId": $STOCKLOT_ID,
    "incoming": $INCOMING,
    "paymentDate": "$PAYMENT_DATE"
}
EOF
)

echo "POST $API_URL"
echo "$JSON_PAYLOAD"

curl -sS -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d "$JSON_PAYLOAD" \
    -w "\nHTTP_STATUS:%{http_code}\n"

curl -sS -X GET "$API_URL" \
    -H "Content-Type: application/json" \
    -d "$JSON_PAYLOAD" \
    -w "\nHTTP_STATUS:%{http_code}\n"

echo "Done."

