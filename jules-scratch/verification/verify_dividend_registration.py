from playwright.sync_api import sync_playwright, expect

def run(playwright):
    browser = playwright.chromium.launch(headless=True)
    context = browser.new_context()
    page = context.new_page()

    try:
        # Navigate to the holding list page
        page.goto("http://localhost:5173/holding")

        # Click the "配当登録" button for the first holding
        # Assuming there is at least one holding in the list
        register_dividend_button = page.locator('button:has-text("配当登録")').first
        register_dividend_button.click()

        # Wait for the navigation to the dividend registration page
        expect(page).to_have_url(lambda url: "/income/add/lot/" in url)

        # Fill in the amount
        amount_input = page.locator('input[type="number"]')
        amount_input.fill("1000")

        # Take a screenshot
        page.screenshot(path="jules-scratch/verification/verification.png")
    finally:
        browser.close()

with sync_playwright() as playwright:
    run(playwright)
