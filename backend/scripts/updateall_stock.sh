#!/bin/bash
#
# Usage:
#   ./curl.sh [stock_code]
#
#   If stock_code is provided, it updates the specific stock.
#   If no stock_code is provided, it updates all stocks.
#

# Stop on error
set -e

# The base URL for the API
BASE_URL="http://localhost:8080/api/stock"

if [ -z "$1" ]; then
  echo "Updating all stocks..."
  curl -X POST "${BASE_URL}/update-all"
else
  echo "Updating stock with code: $1..."
  curl -X POST "${BASE_URL}/$1/update"
fi

echo -e "\nDone."
