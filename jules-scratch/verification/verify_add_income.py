import re
from playwright.sync_api import Page, expect, sync_playwright

def run(playwright):
    browser = playwright.chromium.launch(headless=True)
    context = browser.new_context()
    page = context.new_page()

    # Go to the income list page
    page.goto("http://localhost:5173/income")

    # Click the "新規登録" button
    page.get_by_role("button", name="新規登録").click()

    # Expect the URL to be /income/add
    expect(page).to_have_url(re.compile(".*\/income\/add"))

    # Take a screenshot of the form
    page.screenshot(path="jules-scratch/verification/add_income_form.png")

    browser.close()

with sync_playwright() as playwright:
    run(playwright)
