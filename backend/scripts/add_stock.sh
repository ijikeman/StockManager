#!/bin/bash

# Usage:
# ./add_stock_lot.sh <ownerId> <stockId> <unit> <price> <fee> <isNisa> <transactionDate>
# Example:
# ./add_stock_lot.sh 1 10 100 1234.56 100.00 false 2025-10-09

set -euo pipefail

if [ "$#" -ne 7 ]; then
        echo "Usage: $0 <ownerId> <stockId> <unit> <price> <fee> <isNisa> <transactionDate>"
        echo "Example: $0 1 10 100 1234.56 100.00 false 2025-10-09"
        exit 1
fi

OWNER_ID=$1
STOCK_ID=$2
UNIT=$3
PRICE=$4
FEE=$5
IS_NISA=$6
TRANSACTION_DATE=$7

# Allow overriding the base URL via environment variable, default to localhost
BASE_URL="${BASE_URL:-http://localhost:8080}"
API_URL="$BASE_URL/api/stocklot/add"

JSON_PAYLOAD=$(cat <<EOF
{
    "ownerId": $OWNER_ID,
    "stockId": $STOCK_ID,
    "unit": $UNIT,
    "price": $PRICE,
    "fee": $FEE,
    "isNisa": $IS_NISA,
    "transactionDate": "$TRANSACTION_DATE"
}
EOF
)

echo "POST $API_URL"
echo "$JSON_PAYLOAD"

curl -sS -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d "$JSON_PAYLOAD" \
    -w "\nHTTP_STATUS:%{http_code}\n"

echo "Done."
